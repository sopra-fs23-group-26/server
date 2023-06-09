package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.RoomStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameHistoryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UCService {
    private final Logger log = LoggerFactory.getLogger(UCService.class);

    private final UndercoverRepository undercoverRepository;
    private final RoomRepository roomRepository;

    private final UserRepository userRepository;
    private final RoomService roomService;

    private final GameHistoryRepository gameHistoryRepository;
    @Autowired
    public UCService(@Qualifier("undercoverRepository") UndercoverRepository undercoverRepository,RoomRepository roomRepository, RoomService roomService, UserRepository userRepository,GameHistoryRepository gameHistoryRepository) {
        this.undercoverRepository = undercoverRepository;
        this.roomRepository=roomRepository;
        this.userRepository = userRepository;
        this.gameHistoryRepository = gameHistoryRepository;
        this.roomService=roomService;
    }

    public GameUndercover createGame(Room room){
        //initialize
        room.setRoomStatus(RoomStatus.inGame);
        System.out.println("1");
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setGameStatus(GameStatus.describing);
        gameUndercover.setRoom(room);
        List<User> userlist = new ArrayList<>(gameUndercover.getUsers());
        Collections.sort(userlist, new Comparator<User>() {
            public int compare(User u1, User u2) {
                return Long.compare(u1.getId(), u2.getId());
            }
        });
        gameUndercover.setCurrentPlayerUsername(userlist.get(0).getUsername());

        //allocate undercover and words
        WordSet wordSet = WordSet.generate();
        // Choose a random index for the undercover player
        Random random = new Random();
        int undercoverIndex = random.nextInt(userlist.size());

        // Set the undercover player's role to be UNDERCOVER
        User undercover = userlist.get(undercoverIndex);
        undercover.setUndercover(true);
        undercover.setWord(wordSet.getUndercoverWord());
        undercover.setVoted(false);
        undercover.setVotes(0);
        undercover.setDescription(null);
        userRepository.save(undercover);

        //set others to be detective
        for (int i = 0; i < userlist.size(); i++) {
            if (i != undercoverIndex) {
                userlist.get(i).setUndercover(false);
                userlist.get(i).setWord(wordSet.getDetectiveWord());
                userlist.get(i).setVoted(false);
                userlist.get(i).setVotes(0);
                userlist.get(i).setDescription(null);
                userRepository.save(userlist.get(i));
            }
        }

        undercoverRepository.save(gameUndercover);
        undercoverRepository.flush();
        room.setGameUndercover(gameUndercover);
        log.debug("Created Information for game: {}", gameUndercover);
        return gameUndercover;
    }

    public GameUndercover vote(GameUndercover gameUndercover, List<User> votedUser) {
        //eliminate the votedUser from set users, set its isVoted filed to true
        for(User user : votedUser){
            user.setVoted(true);
            userRepository.save(user);
        }
        //check if the game ends (the undercover has been eliminated or only two)
        Boolean ifGameEnds = ifGameEnds(gameUndercover);
        if(ifGameEnds){
            gameEndsSetting(gameUndercover);
        }else{
            gameUndercover.setGameStatus(GameStatus.describing);
            // start a new round:
            Set<User> users = gameUndercover.getUsers();
            List<User> userlist = new ArrayList<>(users);
            Collections.sort(userlist, new Comparator<User>() {
                public int compare(User u1, User u2) {
                    return Long.compare(u1.getId(), u2.getId());
                }
            });

            // set the current player to first who are not out
            for (User user : userlist) {
                if (!user.isVoted()) {
                    gameUndercover.setCurrentPlayerUsername(user.getUsername());
                    break;
                }
            }
            for (User user : userlist) {
                user.setDescription(null);
                user.setVotes(0);
                userRepository.save(user);
            }
        }
        undercoverRepository.save(gameUndercover);
        return gameUndercover;
    }

    public void gameEndsSetting(GameUndercover gameUndercover) {
        // first set the game status to "gameEnd"
        gameUndercover.setGameStatus(GameStatus.gameEnd);

        // save the game history
        boolean ifUndercoverWin = ifUndercoverWin(gameUndercover);
        if(ifUndercoverWin){
            for(User user: gameUndercover.getUsers()){
                if(user.isUndercover()){
                    createGameHistory("undercover", user.getUsername(), "+5", "WIN");
                    user.setScore(user.getScore()+5);
                }else{
                    createGameHistory("undercover", user.getUsername(), "+0", "LOSE");
                }
            }
        }else{
            for(User user: gameUndercover.getUsers()){
                if(user.isUndercover()){
                    createGameHistory("undercover", user.getUsername(), "+0", "LOSE");
                }else{
                    createGameHistory("undercover", user.getUsername(), "+2", "WIN");
                    user.setScore(user.getScore()+2);
                }
            }
        }

        // clear everything regarding game infos on every user(user is reusable)
        Set<User> users = gameUndercover.getUsers();
        for (User user : users) {
            user.setDescription(null);
            user.setVoted(false);
            user.setRoom(null);
            user.setWord(null);
            user.setUndercover(false);
            user.setVotes(0);
        }
        undercoverRepository.save(gameUndercover);

        deleteAGame(gameUndercover.getId());
        // clear the room
    }

    public void deleteAGame(long gameId){
        GameUndercover gameToDelete = undercoverRepository.findById(gameId);
        if(gameToDelete!=null) {
            Room room = gameToDelete.getRoom();
            gameToDelete.setRoom(null);
            room.setGameUndercover(null);
            undercoverRepository.flush();
            roomRepository.flush();
            roomService.deleteARoom(room.getId());
        }

    }

    private boolean ifUndercoverWin(GameUndercover gameUndercover) {
        boolean result = false;
        for(User user: gameUndercover.getUsers()){
            if (user.isUndercover() && !user.isVoted()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Boolean ifGameEnds(GameUndercover gameUndercover) {
        Set<User> users = gameUndercover.getUsers();
        int numAliveUsers = 0;
        boolean undercoverVoted = false;

        for (User user : users) {
            if (!user.isVoted()) {
                numAliveUsers++;
            }

            if (user.isUndercover() && user.isVoted()) {
                undercoverVoted = true;
            }
        }

        return numAliveUsers <= 2 || undercoverVoted;
    }

    public GameUndercover getGameById(long gameId) {
        return undercoverRepository.findById(gameId);
    }

    /*first check if the current player is the last one,
    * if true, set the game status to voting and reset current player,
    * if false, set the current player to next one*/

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public GameUndercover describe(GameUndercover gameUndercover, User finishedUser) {

        System.out.println("            "+gameUndercover.getId()+"        "+finishedUser.getId());

        //check if finished user is the last element of alivePlayers;
        //boolean ifRoundEnd = ifRoundEnd(gameUndercover, finishedUser);
        //if true, set the game status to voting
        Set<User> users = gameUndercover.getUsers();
        String currentPlayerUsername = gameUndercover.getCurrentPlayerUsername();

        List<User> userlist = new ArrayList<>(users);
        Collections.sort(userlist, new Comparator<User>() {
            public int compare(User u1, User u2) {
                return Long.compare(u1.getId(), u2.getId());
            }
        });

        boolean afterCurrentUser = false;
        boolean ifRoundEnd=true;
        boolean setNextUser=false;

        for (User user : userlist) {
            if(user.getId() == finishedUser.getId()){
                afterCurrentUser=true;
            }
            if(afterCurrentUser){
                if(user.isVoted()==false&&user.getId()!=finishedUser.getId()){
                    ifRoundEnd = false;
                    if(!setNextUser) {
                        gameUndercover.setCurrentPlayerUsername(user.getUsername());
                        setNextUser=true;
                    }
                }
            }
        }

        if (ifRoundEnd) {
            gameUndercover.setGameStatus(GameStatus.voting);
        }
        undercoverRepository.save(gameUndercover);
        return gameUndercover;
    }

    public Boolean voteAndCheckIfEnds(GameUndercover gameUndercover, User votedUser) {
        votedUser.setVotes(votedUser.getVotes()+1);
        userRepository.save(votedUser);
        int notOutPlayersNum = 0;
        int totalVotes = 0;
        for (User user :gameUndercover.getUsers()){
            if(!user.isVoted()){
                notOutPlayersNum = notOutPlayersNum + 1;
                totalVotes = totalVotes + user.getVotes();
            }
        }
        return notOutPlayersNum == totalVotes;
    }

    public List<User> getOutUsers(GameUndercover gameUndercover) {
        // Get all users in the game
        Set<User> players = gameUndercover.getUsers();
        List<User> users = new ArrayList<>(players);

        // Find the maximum number of votes received by a user
        int maxVotes = Integer.MIN_VALUE;
        for (User user : users) {
            if (!user.isVoted() && user.getVotes() > maxVotes) {
                maxVotes = user.getVotes();
            }
        }

        // Collect all users with the maximum number of votes
        List<User> outUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.isVoted() && user.getVotes() == maxVotes) {
                outUsers.add(user);
            }
        }
        return outUsers;
    }

    public void createGameHistory(String gameName, String username, String earnedPoint, String winOrLose){
        GameHistory gameHistory = new GameHistory();
        gameHistory.setUsername(username);
        gameHistory.setEarnedPoint(earnedPoint);
        gameHistory.setGameName(gameName);
        gameHistory.setWinOrLose(winOrLose);
        gameHistory.setTime(LocalDateTime.now());

        gameHistoryRepository.save(gameHistory);
        gameHistoryRepository.flush();
    }

    public User getCurrentPlayer(String name){
        User user=userRepository.findByUsername(name);
        return user;
    }
}
package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class UCService {

    private final Logger log = LoggerFactory.getLogger(UCService.class);

    private final UndercoverRepository undercoverRepository;

    private final UserRepository userRepository;

    @Autowired
    public UCService(@Qualifier("undercoverRepository") UndercoverRepository undercoverRepository, UserRepository userRepository) {
        this.undercoverRepository = undercoverRepository;
        this.userRepository = userRepository;
    }



    public GameUndercover createGame(Room room){
        //initialize
        System.out.println("1");
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setGameStatus(GameStatus.describing);
        gameUndercover.setRoom(room);
        List<User> players = new ArrayList<>(gameUndercover.getUsers());
        gameUndercover.setCurrentPlayerUsername(players.get(0).getUsername());

        //allocate undercover and words
        WordSet wordSet = WordSet.generate();
        // Choose a random index for the undercover player
        Random random = new Random();
        int undercoverIndex = random.nextInt(players.size());

        // Set the undercover player's role to be UNDERCOVER
        User undercover = players.get(undercoverIndex);
        undercover.setUndercover(true);
        undercover.setWord(wordSet.getUndercoverWord());
        undercover.setVoted(false);
        userRepository.save(undercover);

        //set others to be detective
        for (int i = 0; i < players.size(); i++) {
            if (i != undercoverIndex) {
                players.get(i).setUndercover(false);
                players.get(i).setWord(wordSet.getDetectiveWord());
                players.get(i).setVoted(false);
                userRepository.save(players.get(i));
            }
        }
//        gameUndercover = (GameUndercover) undercoverRepository.save(gameUndercover);
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
        }
        //check if the game ends (the undercover has been eliminated or only two)
        Boolean ifGameEnds = ifGameEnds(gameUndercover);
        if(ifGameEnds){
            gameUndercover.setGameStatus(GameStatus.gameEnd);

            Set<User> users = gameUndercover.getUsers();
            for (User user : users) {
                user.setDescription(null);
                user.setVoted(false);
                user.setRoom(null);
                user.setWord(null);
                user.setUndercover(false);
                user.setVotes(0);
            }
        }else{
            // start a new round:
            Set<User> users = gameUndercover.getUsers();

            // set the current player to first who are not out
            for (User user : users) {
                if (!user.isVoted()) {
                    gameUndercover.setCurrentPlayerUsername(user.getUsername());
                    break;
                }
            }
            for (User user : users) {
                user.setDescription(null);
                user.setVotes(0);
            }
        }
        undercoverRepository.save(gameUndercover);
        return gameUndercover;
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
    * if false, set the current player to next one
    */
    public GameUndercover describe(GameUndercover gameUndercover, User finishedUser) {

        //check if finished user is the last element of alivePlayers;
        Boolean ifRoundEnd = ifRoundEnd(gameUndercover, finishedUser);
        //if true, set the game status to voting
        if(ifRoundEnd){
            gameUndercover.setGameStatus(GameStatus.voting);
        }
        //if false, set the current player to the next one
        else{
            Set<User> users = gameUndercover.getUsers();
            String currentPlayerUsername = gameUndercover.getCurrentPlayerUsername();

            for (User user : users) {
                if (Objects.equals(user.getUsername(), currentPlayerUsername)) {
                    // Found the current player, continue iterating to find the next non-voted player.
                    continue;
                }

                if (!user.isVoted()) {
                    // Found a non-voted player after the current player, update currentPlayerId and break out of the loop.
                    gameUndercover.setCurrentPlayerUsername(user.getUsername());
                    break;
                }
            }
        }
        undercoverRepository.save(gameUndercover);
        return gameUndercover;
    }

    //check if finished user is the last who is not out in the players set
    private Boolean ifRoundEnd(GameUndercover gameUndercover, User finishedUser) {
        boolean isLastUserWithFalseVote = true;

        for (User user : gameUndercover.getUsers()) {
            if (user.isVoted()) {
                isLastUserWithFalseVote = false;
                break;
            }

            if (!user.equals(finishedUser)) {
                isLastUserWithFalseVote = false;
            }
        }

        return isLastUserWithFalseVote;
    }

    public Boolean voteAndCheckIfEnds(GameUndercover gameUndercover, User votedUser) {
        votedUser.setVotes(votedUser.getVotes()+1);
        int notOutPlayersNum = 0;
        int totalVotes = 0;
        for (User user :gameUndercover.getUsers()){
            if(!user.isVoted()){
                notOutPlayersNum = notOutPlayersNum + 1;
                totalVotes = totalVotes + user.getVotes();
            }
            if (notOutPlayersNum == totalVotes){
                return true;
            }
        }
        return false;
    }

    public List<User> getOutUsers(GameUndercover gameUndercover) {
        // Get all users in the game
        Set<User> players = gameUndercover.getUsers();
        List<User> users = new ArrayList<>(players);


        // Find the maximum number of votes received by a user
        int maxVotes = Integer.MIN_VALUE;
        for (User user : users) {
            if (user.isVoted() && user.getVotes() > maxVotes) {
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

}

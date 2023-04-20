package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Transactional
public class UCService {

    private final Logger log = LoggerFactory.getLogger(UCService.class);

    private final UndercoverRepository undercoverRepository;

    @Autowired
    public UCService(@Qualifier("undercoverRepository") UndercoverRepository undercoverRepository) {
        this.undercoverRepository = undercoverRepository;
    }



    public GameUndercover createGame(Set<User> users){
        //initialize
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setGameStatus(GameStatus.describing);
        gameUndercover.setUsers(users);
        List<User> players = new ArrayList<>(users);
        gameUndercover.setCurrentPlayerId(players.get(0).getId());

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
        for (int i = 0; i < players.size(); i++) {
            if (i != undercoverIndex) {
                players.get(i).setUndercover(false);
                players.get(i).setWord(wordSet.getDetectiveWord());
                players.get(i).setVoted(false);
            }
        }
//        gameUndercover = (GameUndercover) undercoverRepository.save(gameUndercover);
        undercoverRepository.save(gameUndercover);
        undercoverRepository.flush();
        log.debug("Created Information for game: {}", gameUndercover);
        return gameUndercover;
    }

    public GameUndercover vote(GameUndercover gameUndercover, User votedUser) {
        //eliminate the votedUser from set users, set its isVoted filed to true
        votedUser.setVoted(true);
        //check if the game ends (the undercover has been eliminated or only two)
        Boolean ifGameEnds = ifGameEnds(gameUndercover);
        if(ifGameEnds){
            gameUndercover.setGameStatus(GameStatus.gameEnd);
        }else{
            Set<User> users = gameUndercover.getUsers();

            for (User user : users) {
                if (!user.isVoted()) {
                    gameUndercover.setCurrentPlayerId(user.getId());
                    break;
                }
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
            long currentPlayerId = gameUndercover.getCurrentPlayerId();

            for (User user : users) {
                if (user.getId() == currentPlayerId) {
                    // Found the current player, continue iterating to find the next non-voted player.
                    continue;
                }

                if (!user.isVoted()) {
                    // Found a non-voted player after the current player, update currentPlayerId and break out of the loop.
                    gameUndercover.setCurrentPlayerId(user.getId());
                    break;
                }
            }
        }
        undercoverRepository.save(gameUndercover);
        return gameUndercover;
    }

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

}

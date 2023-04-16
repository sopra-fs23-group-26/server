package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
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
        gameUndercover = (GameUndercover) undercoverRepository.save(gameUndercover);
        undercoverRepository.flush();
        return gameUndercover;
    }


    public GameUndercover vote(GameUndercover gameUndercover, User votedUser) {
        return null;
    }

    public GameUndercover getGameById(long gameId) {
        return undercoverRepository.findById(gameId);
    }

    /*first check if the current player is the last one,
    * if true, set the game status to voting and reset current player,
    * if false, set the current player to next one
    */
    public GameUndercover describe(GameUndercover gameUndercover, User finishedUser) {

        return null;
    }
}

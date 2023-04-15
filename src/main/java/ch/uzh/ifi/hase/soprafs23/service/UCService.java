package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class UCService {

    public GameUndercover createGame(Room room){
        //initialize
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setGameStatus(GameStatus.describing);
        gameUndercover.setUsers(room.getPlayers());
        List<User> players = new ArrayList<>(room.getPlayers());
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
        for (int i = 0; i < players.size(); i++) {
            if (i != undercoverIndex) {
                players.get(i).setUndercover(false);
                players.get(i).setWord(wordSet.getDetectiveWord());
            }
        }
        return gameUndercover;
    }


}

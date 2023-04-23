package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.WordSet;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UCServiceTest {

    @Mock
    UndercoverRepository undercoverRepository;

    @InjectMocks
    UCService ucService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void createGame_validInput_gameCreated() {
        // Setup
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setId(1L);
        users.add(user1);
        User user2 = new User();
        user2.setId(2L);
        users.add(user2);

        GameUndercover expectedGame = new GameUndercover();
        expectedGame.setGameStatus(GameStatus.describing);
        expectedGame.setUsers(users);
        List<User> players = new ArrayList<>(users);
        expectedGame.setCurrentPlayerUsername(players.get(0).getUsername());

        WordSet wordSet = WordSet.generate();
        Random random = new Random();
        int undercoverIndex = random.nextInt(players.size());
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

        when(undercoverRepository.save(expectedGame)).thenReturn(expectedGame);

        // Execute
        GameUndercover actualGame = ucService.createGame(users);

        // Verify
        assertNotNull(actualGame);
        assertEquals(expectedGame.getGameStatus(), actualGame.getGameStatus());
        assertEquals(expectedGame.getUsers(), actualGame.getUsers());
        assertEquals(expectedGame.getCurrentPlayerUsername(), actualGame.getCurrentPlayerUsername());
    }



}
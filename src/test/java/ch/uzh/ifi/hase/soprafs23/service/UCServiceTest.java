package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs23.entity.GameUndercover;
import ch.uzh.ifi.hase.soprafs23.entity.Room;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameHistoryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.RoomRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UCServiceTest {

    @Mock
    UndercoverRepository undercoverRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    RoomRepository roomRepository;
    @Mock
    GameHistoryRepository gameHistoryRepository;

    @InjectMocks
    UCService ucService;
    @InjectMocks
    RoomService roomService;
    @InjectMocks
    GameHistoryService gameHistoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void testCreateGameNew() {
        Room room = new Room();
        room.setId(1L);
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);
        user2.setWord("word2");
        users.add(user1);
        users.add(user2);
        room.setPlayers(users);
        when(roomService.getRoomById(1L)).thenReturn(room);

        GameUndercover gameUndercover = ucService.createGame(room);

        assertNotNull(gameUndercover);
        assertEquals(GameStatus.describing, gameUndercover.getGameStatus());
        assertEquals(room, gameUndercover.getRoom());
        assertNotNull(gameUndercover.getUsers());
        assertFalse(gameUndercover.getUsers().isEmpty());
        assertEquals(gameUndercover, room.getGameUndercover());
    }

    @Test
    void testCreateGameExisting() {
        Room room = new Room();
        room.setId(1L);
        GameUndercover existingGame = new GameUndercover();
        room.setGameUndercover(existingGame);
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");
        user1.setId(1L);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);
        user2.setWord("word2");
        user2.setId(2L);

        users.add(user1);
        users.add(user2);
        room.setPlayers(users);
        when(roomService.getRoomById(1L)).thenReturn(room);

        GameUndercover gameUndercover = ucService.createGame(room);

        assertEquals(existingGame.getId(), gameUndercover.getId());
    }

    @Test
    public void testVoteIfGameEnds() {
        // Create a new game with two players
        GameUndercover game = new GameUndercover();
        Room room = new Room();
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");
        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);
        user2.setWord("word2");
        users.add(user1);
        users.add(user2);
        room.setPlayers(users);
        game.setGameStatus(GameStatus.describing);
        game.setRoom(room);
        room.setGameUndercover(game);

        // Vote off the undercover player
        List<User> votedUsers = new ArrayList<>();
        votedUsers.add(user2);
        game = ucService.vote(game, votedUsers);

        // Check that the game has ended and all players have been reset
        assertEquals(GameStatus.gameEnd, game.getGameStatus());
        for (User user : game.getUsers()) {
            assertNull(user.getDescription());
            assertFalse(user.isVoted());
            assertNull(user.getRoom());
            assertNull(user.getWord());
            assertFalse(user.isUndercover());
            assertEquals(0, user.getVotes());
        }
    }

    @Test
    public void testVoteIfGameContinues() {
        // Create a new game with three players
        GameUndercover game = new GameUndercover();
        Room room = new Room();
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");
        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);// undercover
        user2.setWord("word2");
        User user3 = new User();
        user3.setUsername("user3");
        user3.setVoted(false);
        user3.setUndercover(false);
        user3.setWord("word3");
        User user4 = new User();
        user4.setVoted(false);
        user4.setUndercover(false);
        user4.setWord("word3");
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        room.setPlayers(users);
        game.setGameStatus(GameStatus.describing);
//        game.setCurrentPlayerUsername(user1.getUsername());
        game.setRoom(room);
        room.setGameUndercover(game);

        // Vote off one of the players
        List<User> votedUsers = new ArrayList<>();
        votedUsers.add(user1);
//        votedUsers.add(user2);
//        votedUsers.add(user3);
//        votedUsers.add(user4);
//        game = ucService.vote(game, votedUsers);
//        assertEquals(game.getGameStatus(), GameStatus.describing);

    }



    @Test
    void testDescribeRoundEnd() {
        // create game with 3 players, current player is the second one
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");
        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);
        user2.setWord("word2");
        User user3 = new User();
        user3.setUsername("user3");
        user3.setVoted(false);
        user3.setUndercover(false);
        user3.setWord("word3");
        User user4 = new User();
        user4.setVoted(false);
        user4.setUndercover(false);
        user4.setWord("word3");
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setGameStatus(GameStatus.describing);
        undercoverRepository.save(gameUndercover);

        // check that the game status is now voting
        //assertEquals(GameStatus.voting, gameUndercover.getGameStatus());
    }


    @Test
    void testGameEndsSettingUndercoverWin(){
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(true);
        user1.setUndercover(false);
        user1.setWord("word1");


        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(false);
        user2.setUndercover(true);// under cover
        user2.setWord("word2");

        users.add(user1);
        users.add(user2);

        Room room = new Room();
        room.setPlayers(users);

        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setRoom(room);

        gameUndercover.setGameStatus(GameStatus.describing);
        undercoverRepository.save(gameUndercover);

        ucService.gameEndsSetting(gameUndercover);

        assertNull(user1.getWord());
        assertEquals(false, user1.isVoted());
        assertEquals(false, user2.isUndercover());
        assertEquals(GameStatus.gameEnd, gameUndercover.getGameStatus());

    }

    @Test
    void testGameEndsSettingUndercoverLose(){
        Set<User> users = new HashSet<>();
        User user1 = new User();
        user1.setUsername("user1");
        user1.setVoted(false);
        user1.setUndercover(false);
        user1.setWord("word1");


        User user2 = new User();
        user2.setUsername("user2");
        user2.setVoted(true);
        user2.setUndercover(true);// under cover
        user2.setWord("word2");

        users.add(user1);
        users.add(user2);

        Room room = new Room();
        room.setPlayers(users);

        GameUndercover gameUndercover = new GameUndercover();
        gameUndercover.setRoom(room);

        gameUndercover.setGameStatus(GameStatus.describing);
        undercoverRepository.save(gameUndercover);

        ucService.gameEndsSetting(gameUndercover);

        assertNull(user1.getWord());
        assertEquals(false, user2.isVoted());
        assertEquals(false, user2.isUndercover());
        assertEquals(GameStatus.gameEnd, gameUndercover.getGameStatus());

    }


   @Test
    void testGetGameById(){
       Set<User> users = new HashSet<>();
       User user1 = new User();
       user1.setUsername("user1");
       user1.setVoted(true);
       user1.setUndercover(false);
       user1.setWord("word1");


       User user2 = new User();
       user2.setUsername("user2");
       user2.setVoted(false);
       user2.setUndercover(true);// under cover
       user2.setWord("word2");

       users.add(user1);
       users.add(user2);

       Room room = new Room();
       room.setPlayers(users);

       GameUndercover gameUndercover = new GameUndercover();
       gameUndercover.setRoom(room);

       gameUndercover.setGameStatus(GameStatus.describing);
       undercoverRepository.save(gameUndercover);

       ucService.gameEndsSetting(gameUndercover);

       when(undercoverRepository.findById(anyLong())).thenReturn(gameUndercover);

       assertEquals(gameUndercover, ucService.getGameById(anyLong()));


   }

   @Test
    void testVoteAndCheckIfEndsTrue(){

       Set<User> users = new HashSet<>();
       User user1 = new User();
       user1.setUsername("user1");
       user1.setVoted(false);
       user1.setUndercover(false);
       user1.setWord("word1");
       user1.setVotes(1);


       User user2 = new User();
       user2.setUsername("user2");
       user2.setVoted(false);
       user2.setUndercover(true);// under cover
       user2.setWord("word2");

       users.add(user1);
       users.add(user2);

       Room room = new Room();
       room.setPlayers(users);

       GameUndercover gameUndercover = new GameUndercover();
       gameUndercover.setRoom(room);

       gameUndercover.setGameStatus(GameStatus.describing);
       undercoverRepository.save(gameUndercover);

       assertEquals(true, ucService.voteAndCheckIfEnds(gameUndercover, user1));
   }


   @Test
    void testGetOutUsers(){
       Set<User> users = new HashSet<>();
       User user1 = new User();
       user1.setUsername("user1");
       user1.setVoted(false);
       user1.setUndercover(false);
       user1.setWord("word1");
       user1.setVotes(2);


       User user2 = new User();
       user2.setUsername("user2");
       user2.setVoted(false);
       user2.setUndercover(true);// under cover
       user2.setWord("word2");
       user2.setVotes(0);

       users.add(user1);
       users.add(user2);

       Room room = new Room();
       room.setPlayers(users);

       GameUndercover gameUndercover = new GameUndercover();
       gameUndercover.setRoom(room);

       gameUndercover.setGameStatus(GameStatus.describing);
       undercoverRepository.save(gameUndercover);

       List<User> result = new ArrayList<>();
       result.add(user1);
       assertEquals(result, ucService.getOutUsers(gameUndercover));
   }


   @Test
    void testGetCurrentPlayer(){
       User user1 = new User();
       user1.setUsername("user1");
       user1.setVoted(false);
       user1.setUndercover(false);
       user1.setWord("word1");
       user1.setVotes(2);

       when(userRepository.findByUsername(anyString())).thenReturn(user1);

       assertEquals(ucService.getCurrentPlayer(anyString()), user1);
   }

   @Test
    void testCreateGameHistory(){
        ucService.createGameHistory("undercover", "user1", "point2", "W");
        assertNotNull(gameHistoryRepository.findByUsername("user1"));
   }

   @Test
    void testDescribe_GameStatus_Describing_and_Voting(){
       Set<User> users = new HashSet<>();
       User user1 = new User();
       user1.setId(1L);
       user1.setUsername("user1");
       user1.setVoted(false);
       user1.setUndercover(false);
       user1.setWord("word1");
       user1.setVotes(2);


       User user2 = new User();
       user2.setId(2L);
       user2.setUsername("user2");
       user2.setVoted(false);
       user2.setUndercover(true);// under cover
       user2.setWord("word2");
       user2.setVotes(0);

       users.add(user1);
       users.add(user2);

       Room room = new Room();
       room.setPlayers(users);

       GameUndercover gameUndercover = new GameUndercover();
       gameUndercover.setRoom(room);

       gameUndercover.setGameStatus(GameStatus.describing);
       undercoverRepository.save(gameUndercover);

       GameUndercover game = ucService.describe(gameUndercover, user1);
       assertEquals(GameStatus.describing, game.getGameStatus());


       User user3 = new User();
       user2.setId(3L);
       user2.setUsername("user3");
       user2.setVoted(false);
       user2.setUndercover(true);// under cover
       user2.setWord("word2");
       user2.setVotes(0);

       GameUndercover game_ = ucService.describe(gameUndercover, user3);
       assertEquals(GameStatus.voting, game_.getGameStatus());



   }
}
package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.GameHistory;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;

    @Autowired
    public GameHistoryService(@Qualifier("gameHistoryRepository") GameHistoryRepository gameHistoryRepository) {
        this.gameHistoryRepository = gameHistoryRepository;
    }

    public GameHistory getLatestGameHistory(User user) {
        List<GameHistory> historyList = gameHistoryRepository.findByUsername(user.getUsername());

        // Check if the historyList is not empty
        if (!historyList.isEmpty()) {
            // Sort the historyList in descending order of time and get the first element
            return historyList.stream().max(Comparator.comparing(GameHistory::getTime)).orElse(null);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find this user's game history!");
        }
    }

    public List<GameHistory> getGameHistory(User user) {
        List<GameHistory> historyList = gameHistoryRepository.findByUsername(user.getUsername());
        if (!historyList.isEmpty()) {
            return historyList;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find this user's game history!");
        }
    }
}
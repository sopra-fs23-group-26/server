package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.repository.UndercoverRepository;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UCServiceTest {

    @Mock
    UndercoverRepository undercoverRepository;

    @InjectMocks
    UCService ucService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }
}
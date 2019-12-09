package com.nsweb.heroapp;


import com.nsweb.heroapp.data.repositories.SuperHeroRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
// This is an example of a Junit5 test
@ExtendWith(MockitoExtension.class)
class ExampleUnitTest {

    @Mock
    private SuperHeroRepository superHeroRepository;

    @Test
    void shouldTestMock() {
        assertThat(superHeroRepository).isNotNull();
    }

    @Test
    void testTrueIsTrue() {
        assertThat(true).isTrue();
    }
}
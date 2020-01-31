package com.nsweb.heroapp;


import androidx.lifecycle.LifecycleOwner;

import com.nsweb.heroapp.data.repositories.SuperHeroRepository;
import com.nsweb.heroapp.data.retrofit.configuration.RetrofitInstance;
import com.nsweb.heroapp.viewmodel.SuperHeroViewModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
// This is an example of a Junit5 test
@ExtendWith(MockitoExtension.class)
class ExampleUnitTest {

    @Mock
    private RetrofitInstance retrofitInstance;

    @Mock
    private SuperHeroRepository superHeroRepository;

    @Mock
    private SuperHeroViewModel superHeroViewModel;

    @Mock
    LifecycleOwner lifecycleOwner;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldTestMock() {
        assertThat(superHeroRepository).isNotNull();
    }

    @Test
    void letsTestSomething() {
        assertThat(superHeroViewModel).isNotNull();
    }

    @Test
    void testGettingSuperHeroes() {
        assertNotNull(retrofitInstance);
    }

    @Test
    void testTrueIsTrue() {
        assertThat(true).isTrue();
    }
}
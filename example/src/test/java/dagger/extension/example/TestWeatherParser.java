package dagger.extension.example;

import android.content.Context;

import com.bumptech.glide.RequestManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
//import dagger.extension.example.di.TestWeatherApplication;
import dagger.extension.example.model.forecast.threehours.ThreeHoursForecastWeather;
import dagger.extension.example.service.DateProvider;
import dagger.extension.example.service.filter.TodayWeatherResponseFilter;
import dagger.extension.example.service.filter.TomorrowWeatherResponseFilter;
import dagger.extension.example.stubs.DateProviderStub;
import dagger.extension.example.stubs.Responses;
import dagger.extension.example.view.main.MainActivity;
import dagger.extension.example.view.weather.TodayWeatherFragment;
import dagger.extension.example.view.weather.TodayWeatherViewModel;

import static dagger.extension.example.stubs.Fakes.dateProvider;
import static dagger.extension.example.stubs.Fakes.fakeResponse;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestWeatherParser
{

    private TodayWeatherResponseFilter todayWeatherResponseFilter;
    private TomorrowWeatherResponseFilter tomorrowWeatherResponseFilter;

    @Before
    public void setUp() throws Exception {

    }

    private void createFilters(DateProvider dateProvider) {
        todayWeatherResponseFilter = new TodayWeatherResponseFilter(dateProvider);
        tomorrowWeatherResponseFilter = new TomorrowWeatherResponseFilter(dateProvider);
    }

    @Test
    public void itShouldFilterOutAllRecordsOlderThanProvidedDate() throws ParseException, IOException {
        DateProviderStub dateProvider = dateProvider(2016, Calendar.DECEMBER, 23, 12, 0, 1);
        createFilters(dateProvider);
        ThreeHoursForecastWeather body = fakeResponse(Responses.JSON.FORECAST_RESULT);
        String actual = todayWeatherResponseFilter.parse(body);
        assertEquals(Responses.createExpectedFilteredResult(), actual);
    }

    @Test
    public void itShouldRecognizeYearChange() throws ParseException, IOException {
        DateProviderStub dateProvider = dateProvider(2016, Calendar.DECEMBER, 31, 12, 0, 0);
        createFilters(dateProvider);
        ThreeHoursForecastWeather body = fakeResponse(Responses.JSON.FORECAST_WITH_YEAR_CHANGE);
        String actual = tomorrowWeatherResponseFilter.parse(body);
        assertEquals(new StringBuilder()
                .append("2017-01-01 00:00:00: -11.13°C").append("\n")
                .append("2017-01-01 03:00:00: -11.27°C").append("\n")
                .toString(), actual);
    }

}

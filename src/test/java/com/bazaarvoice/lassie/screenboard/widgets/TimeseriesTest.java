package com.bazaarvoice.lassie.screenboard.widgets;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class TimeseriesTest {
    private ObjectMapper _json;

    /**
     *
     */
    @Before
    public void before() {
        _json = new ObjectMapper();
    }

    /** @throws Exception  */
    @Test
    public void timeSeriesSerialization() throws Exception {
        Timeseries testTimeseries = new Timeseries();
        testTimeseries.getTileDefinition().getEvents().add(new Query(""));
        testTimeseries.getTileDefinition().getRequests().add(Request.create(Aggregator.AVERAGE, "test.user.data"));
        assertEquals("{\"type\":\"timeseries\",\"height\":13,\"width\":47,\"x\":0,\"y\":0,\"title_size\":16,\"title\":true,\"title_align\":\"left\"," +
                "\"title_text\":\"Title\",\"timeframe\":\"1w\",\"tile_def\":" +
                "{\"viz\":\"timeseries\",\"requests\":[{\"stacked\":false,\"q\":\"avg:test.user.data{*}\"}],\"events\":[{\"q\":\"\"}]}}", _json.writeValueAsString(testTimeseries));
    }

    /** @throws Exception  */
    @Test
    public void stackedTimeSeriesSerialization() throws Exception {
        Timeseries testTimeseries = new Timeseries();
        testTimeseries.getTileDefinition().getEvents().add(new Query(""));
        testTimeseries.getTileDefinition().getRequests().add(Request.create(Aggregator.AVERAGE, "test.user.data", "test.user.breakdown", true));
        assertEquals("{\"type\":\"timeseries\",\"height\":13,\"width\":47,\"x\":0,\"y\":0,\"title_size\":16,\"title\":true,\"title_align\":\"left\"," +
                "\"title_text\":\"Title\",\"timeframe\":\"1w\",\"tile_def\":" +
                "{\"viz\":\"timeseries\",\"requests\":[{\"stacked\":true,\"q\":\"avg:test.user.data{*} by {test.user.breakdown}\"}],\"events\":[{\"q\":\"\"}]}}", _json.writeValueAsString(testTimeseries));
    }

    /** @throws Exception  */
    @Test
    public void fullTimeSeriesConstructerSerialization() throws Exception {
        Timeseries testTimeseries = new Timeseries();
        testTimeseries.getTileDefinition().getEvents().add(new Query(""));
        testTimeseries.getTileDefinition().getRequests().add(Request.create(Aggregator.AVERAGE, "test.user.data", "over", "test.user.breakdown", true));
        assertEquals("{\"type\":\"timeseries\",\"height\":13,\"width\":47,\"x\":0,\"y\":0,\"title_size\":16,\"title\":true,\"title_align\":\"left\"," +
                "\"title_text\":\"Title\",\"timeframe\":\"1w\",\"tile_def\":" +
                "{\"viz\":\"timeseries\",\"requests\":[{\"stacked\":true,\"q\":\"avg:test.user.data{over} by {test.user.breakdown}\"}],\"events\":[{\"q\":\"\"}]}}", _json.writeValueAsString(testTimeseries));
    }

    /** @throws Exception  */
    @Test
    public void timeSeriesDeserialization() throws Exception {
        Timeseries testTimeseries = (Timeseries) _json.readValue(
                "{\"type\":\"timeseries\",\"height\":14,\"width\":47,\"x\":1,\"y\":1,\"title_size\":17,\"title\":false,\"title_align\":\"right\"," +
                        "\"title_text\":\"Not My Metric\",\"timeframe\":\"1d\",\"tile_def\":" +
                        "{\"viz\":\"timeseries\",\"requests\":[{\"q\":\"query1\",\"stacked\":true}],\"events\":[{\"q\":\"query2\"}]}}", Widget.class);

        assertEquals(14, testTimeseries.getDimensions().getHeight());
        assertEquals(47, testTimeseries.getDimensions().getWidth());
        assertEquals(1, testTimeseries.getLocation().getX());
        assertEquals(1, testTimeseries.getLocation().getY());
        assertEquals(17, testTimeseries.getTitleSize());
        assertEquals(false, testTimeseries.isTitleVisible());
        assertEquals(Widget.Alignment.RIGHT, testTimeseries.getTitleAlignment());
        assertEquals("Not My Metric", testTimeseries.getTitle());
        assertEquals(Timeframe.ONE_DAY, testTimeseries.getTimeframe());

        TileDefinition tileDefinition = testTimeseries.getTileDefinition();
        List<Query> testEvents = tileDefinition.getEvents();
        List<Request> testRequests = tileDefinition.getRequests();

        assertEquals("query1", testRequests.get(0).getQuery());
        assertEquals("query2", testEvents.get(0).getValue());
    }
}

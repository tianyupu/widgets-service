package com.tianyupu.widgets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class DefaultWidgetServiceTest {
    private DefaultWidgetService defaultWidgetService;

    @Before
    public void setup() {
        defaultWidgetService = new DefaultWidgetService();
    }

    @Test
    public void shouldCreateWidgetGivenFullySpecifiedWidgetRequest() {
        Widget createdWidget = defaultWidgetService.createWidget(defaultWidgetRequest());

        assertThat(createdWidget.getX(), is(10));
        assertThat(createdWidget.getY(), is(20));
        assertThat(createdWidget.getWidth(), is(100));
        assertThat(createdWidget.getHeight(), is(200));
        assertThat(createdWidget.getZIndex(), is(5));
    }

    @Test
    public void shouldGenerateIncrementalIdsForConsecutivelyCreativeWidgets() {
        Widget firstCreatedWidget = defaultWidgetService.createWidget(defaultWidgetRequest());
        Widget secondCreatedWidget = defaultWidgetService.createWidget(defaultWidgetRequest());

        assertThat(firstCreatedWidget.getId() + 1, is(secondCreatedWidget.getId()));
    }

    @Test
    public void shouldUseTheBiggestZIndexWhenCreatingANewWidgetIfNoZIndexIsSpecified() {
        WidgetRequest widgetRequestWithNoZIndex = defaultWidgetRequestWithZIndex(null);

        Widget firstCreatedWidget = defaultWidgetService.createWidget(defaultWidgetRequest());
        Widget secondCreatedWidget = defaultWidgetService.createWidget(widgetRequestWithNoZIndex);

        assertThat(firstCreatedWidget.getZIndex(), is(5));
        assertThat(secondCreatedWidget.getZIndex(), is(6));
    }

    @Test
    public void shouldUpdateZIndexesOfAllHigherWidgetsWhenANewWidgetIsAdded() {
        WidgetRequest widgetRequest1 = defaultWidgetRequestWithZIndex(2);
        WidgetRequest widgetRequest2 = defaultWidgetRequestWithZIndex(6);
        WidgetRequest widgetRequest3 = defaultWidgetRequestWithZIndex(-1);

        Widget firstCreatedWidget = defaultWidgetService.createWidget(widgetRequest1);
        Widget secondCreatedWidget = defaultWidgetService.createWidget(widgetRequest2);
        Widget thirdCreatedWidget = defaultWidgetService.createWidget(widgetRequest3);

        assertThat(thirdCreatedWidget.getZIndex(), is(-1));
        assertThat(firstCreatedWidget.getZIndex(), is(3));
        assertThat(secondCreatedWidget.getZIndex(), is(7));
    }

    @Test
    public void shouldRetrieveCorrectWidgetWhenGivenTheWidgetId() {
        WidgetRequest widgetRequest1 = defaultWidgetRequestWithZIndex(2);
        WidgetRequest widgetRequest2 = defaultWidgetRequestWithZIndex(6);
        defaultWidgetService.createWidget(widgetRequest1);
        defaultWidgetService.createWidget(widgetRequest2);

        Widget widget = defaultWidgetService.getWidgetById(2);

        assertThat(widget.getZIndex(), is(6));
    }

    @Test
    public void shouldUpdateGivenWidgetWithProvidedValues() {
        defaultWidgetService.createWidget(defaultWidgetRequest());

        WidgetRequest updateWidgetRequest = new WidgetRequest();
        updateWidgetRequest.setX(40);
        updateWidgetRequest.setY(20);
        updateWidgetRequest.setWidth(5);
        updateWidgetRequest.setHeight(10);
        updateWidgetRequest.setZIndex(1);
        Widget updatedWidget = defaultWidgetService.updateWidgetById(1L, updateWidgetRequest);

        assertThat(updatedWidget.getX(), is(40));
        assertThat(updatedWidget.getY(), is(20));
        assertThat(updatedWidget.getWidth(), is(5));
        assertThat(updatedWidget.getHeight(), is(10));
        assertThat(updatedWidget.getZIndex(), is(1));
    }

    @Test
    public void shouldRemoveWidgetWithGivenId() {
        defaultWidgetService.createWidget(defaultWidgetRequest());

        Widget deletedWidget = defaultWidgetService.deleteWidgetById(1L);

        assertThat(deletedWidget.getId(), is(1L));
    }

    @Test
    public void shouldReturnAListOfWidgetsSortedByZIndexInAscendingOrder() {
        Widget widget1 = defaultWidgetService.createWidget(defaultWidgetRequestWithZIndex(10));
        Widget widget2 = defaultWidgetService.createWidget(defaultWidgetRequestWithZIndex(1));
        Widget widget3 = defaultWidgetService.createWidget(defaultWidgetRequestWithZIndex(-20));

        List<Widget> widgetList = defaultWidgetService.getAllWidgets();

        assertThat(widgetList, is(newArrayList(widget3, widget2, widget1)));
    }

    private WidgetRequest defaultWidgetRequest() {
        WidgetRequest widgetRequest = new WidgetRequest();
        widgetRequest.setX(10);
        widgetRequest.setY(20);
        widgetRequest.setWidth(100);
        widgetRequest.setHeight(200);
        widgetRequest.setZIndex(5);

        return widgetRequest;
    }

    private WidgetRequest defaultWidgetRequestWithZIndex(Integer zIndex) {
        WidgetRequest widgetRequest = defaultWidgetRequest();
        widgetRequest.setZIndex(zIndex);
        return widgetRequest;
    }
}

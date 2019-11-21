package com.tianyupu.widgets;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class DefaultWidgetService implements WidgetService {
    private static final int PARALLELISM_THRESHOLD = 1;
    private final AtomicLong counter = new AtomicLong();
    private final ConcurrentHashMap<Long, Widget> widgets = new ConcurrentHashMap<>();

    @Override
    public Widget createWidget(WidgetRequest widgetRequest) {
        long newId = counter.incrementAndGet();
        Widget newWidget = widgets.computeIfAbsent(newId, id -> new Widget(
                widgetRequest.getX(),
                widgetRequest.getY(),
                widgetRequest.getWidth(),
                widgetRequest.getHeight(),
                Optional.ofNullable(widgetRequest.getZIndex()).orElse(getMaxZIndex() + 1),
                id
        ));
        updateZIndexes(newWidget, 1);
        return newWidget;
    }

    @Override
    public Widget getWidgetById(long id) {
        return widgets.get(id);
    }

    @Override
    public Widget updateWidgetById(long id, WidgetRequest widgetRequest) {
        Widget widget = getWidgetById(id);

        widget.setX(widgetRequest.getX());
        widget.setY(widgetRequest.getY());
        widget.setWidth(widgetRequest.getWidth());
        widget.setHeight(widgetRequest.getHeight());
        widget.setZIndex(widgetRequest.getZIndex());

        return widget;
    }

    @Override
    public Widget deleteWidgetById(long id) {
        return widgets.remove(id);
    }

    @Override
    public List<Widget> getAllWidgets() {
        return widgets.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().getZIndex()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private int getMaxZIndex() {
        return widgets.reduceValuesToInt(PARALLELISM_THRESHOLD, Widget::getZIndex, Integer.MIN_VALUE, Math::max);
    }

    private void updateZIndexes(Widget newWidget, int delta) {
        widgets.forEach(
                PARALLELISM_THRESHOLD,
                (existingId, existingWidget) -> {
                    if (existingWidget.getZIndex() >= newWidget.getZIndex()
                            && !existingWidget.equals(newWidget)) {
                        existingWidget.setZIndex(existingWidget.getZIndex() + delta);
                    }
                }
        );
    }
}

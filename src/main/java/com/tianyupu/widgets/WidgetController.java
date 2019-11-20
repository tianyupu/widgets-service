package com.tianyupu.widgets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/*
TODO:
    - Move the logic into a service

Questions to be answered:
    - When changing a widget, will we receive a new widget spec, or just the
      fields that need to be updated?
    - When deleting, do we return the representation of the deleted widget?
    - What is expected when the widget with a given id is not found?
    - Is the last modified time only updated if the widget has been updated via
      a request, or is it any update, including when it's updated as a side
      effect of another widget update/insert?
 */
@RestController
public class WidgetController {
    private static final int PARALLELISM_THRESHOLD = 1;
    private final AtomicLong counter = new AtomicLong();
    private final ConcurrentHashMap<Long, Widget> widgets = new ConcurrentHashMap<>();

    @RequestMapping(path="/widget", method=POST)
    public ResponseEntity<Widget> createWidget(@RequestBody WidgetRequest widgetRequest) {
        if (widgetRequest.getX() == null
                || widgetRequest.getY() == null
                || widgetRequest.getWidth() == null
                || widgetRequest.getHeight() == null) {
            return ResponseEntity.badRequest().build();
        }
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newWidget);
    }

    @RequestMapping(path="/widget/{id}", method=GET)
    public ResponseEntity<Widget> getWidgetById(@PathVariable(value="id") long id) {
        return Optional.ofNullable(widgets.get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(path="/widget/{id}", method=PUT)
    public ResponseEntity<Widget> updateWidgetById(
            @PathVariable(value="id") long id,
            @RequestBody WidgetRequest widgetRequest
    ) {
        if (widgetRequest.getX() == null
                || widgetRequest.getY() == null
                || widgetRequest.getWidth() == null
                || widgetRequest.getHeight() == null) {
            return ResponseEntity.badRequest().build();
        }

        Widget widget = widgets.get(id);
        if (widget == null) {
            return ResponseEntity.notFound().build();
        }

        widget.setX(widgetRequest.getX());
        widget.setY(widgetRequest.getY());
        widget.setWidth(widgetRequest.getWidth());
        widget.setHeight(widgetRequest.getHeight());

        int previousZIndex = widget.getZIndex();
        Integer newZIndex = Optional.ofNullable(widgetRequest.getZIndex()).orElse(getMaxZIndex() + 1);
        widget.setZIndex(newZIndex);
        updateZIndexes(widget, newZIndex - previousZIndex);

        return ResponseEntity.ok(widget);
    }

    @RequestMapping(path="/widget/{id}", method=DELETE)
    public ResponseEntity<Widget> deleteWidgetById(@PathVariable(value="id") long id) {
        return Optional.ofNullable(widgets.remove(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(path="/widgets", method=GET)
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

    private void updateZIndexes(Widget updatedWidget, int delta) {
        widgets.forEach(
                PARALLELISM_THRESHOLD,
                (existingId, existingWidget) -> {
                    if (existingWidget.getZIndex() > updatedWidget.getZIndex() - delta
                            && !existingWidget.equals(updatedWidget)) {
                        existingWidget.setZIndex(existingWidget.getZIndex() + delta);
                    }
                }
        );
    }
}

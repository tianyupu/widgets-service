package com.tianyupu.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class WidgetController {
    private final WidgetService widgetService;

    @Autowired
    public WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }

    @RequestMapping(path="/widget", method=POST)
    public ResponseEntity<Widget> createWidget(@RequestBody WidgetRequest widgetRequest) {
        if (widgetRequest.getX() == null
                || widgetRequest.getY() == null
                || widgetRequest.getWidth() == null
                || widgetRequest.getHeight() == null) {
            return ResponseEntity.badRequest().build();
        }

        Widget newWidget = widgetService.createWidget(widgetRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(newWidget);
    }

    @RequestMapping(path="/widget/{id}", method=GET)
    public ResponseEntity<Widget> getWidgetById(@PathVariable(value="id") long id) {
        return Optional.ofNullable(widgetService.getWidgetById(id))
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

        if (widgetService.getWidgetById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        Widget updatedWidget = widgetService.updateWidgetById(id, widgetRequest);
        return ResponseEntity.ok(updatedWidget);
    }

    @RequestMapping(path="/widget/{id}", method=DELETE)
    public ResponseEntity<Widget> deleteWidgetById(@PathVariable(value="id") long id) {
        return Optional.ofNullable(widgetService.deleteWidgetById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(path="/widgets", method=GET)
    public List<Widget> getAllWidgets() {
        return widgetService.getAllWidgets();
    }
}

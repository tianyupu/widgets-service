package com.tianyupu.widgets;

import java.util.List;

public interface WidgetService {
    Widget createWidget(WidgetRequest widgetRequest);

    Widget getWidgetById(long id);

    Widget updateWidgetById(long id, WidgetRequest widgetRequest);

    Widget deleteWidgetById(long id);

    List<Widget> getAllWidgets();
}

package com.tianyupu.widgets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class WidgetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnBadRequestWhenCreatingAWidgetIfTheSpecificationIsMissingRequiredFields() throws Exception {
        String incompleteRequestBody = "{ \"x\": 10, \"height\": 100 }";

        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(incompleteRequestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnWidgetInResponseBodyAndCreatedResponseIfWidgetIsCreatedSuccessfully() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";

        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(completeRequestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(20))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(100))
                .andExpect(jsonPath("$.zindex").value(5));
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToRetrieveANonexistentWidget() throws Exception {
        mockMvc.perform(get("/widget/42"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnWidgetInResponseBodyWhenRetrievingAValidWidget() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";
        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(completeRequestBody));

        mockMvc.perform(get("/widget/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(20))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(100))
                .andExpect(jsonPath("$.zindex").value(5));
    }

    @Test
    public void shouldReturnBadRequestWhenUpdatingAWidgetIfTheSpecificationIsMissingRequiredFields() throws Exception {
        String incompleteRequestBody = "{ \"x\": 10, \"height\": 100 }";

        mockMvc.perform(put("/widget/1").contentType(APPLICATION_JSON).content(incompleteRequestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToUpdateANonexistentWidget() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";

        mockMvc.perform(put("/widget/1").contentType(APPLICATION_JSON).content(completeRequestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnWidgetInResponseBodyAfterUpdatingIt() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";
        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(completeRequestBody));

        String completeUpdateRequestBody = "{ \"x\": 15, \"y\": 25, \"width\": 205, \"height\": 105, \"zindex\": 10 }";
        mockMvc.perform(put("/widget/1").contentType(APPLICATION_JSON).content(completeUpdateRequestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(15))
                .andExpect(jsonPath("$.y").value(25))
                .andExpect(jsonPath("$.width").value(205))
                .andExpect(jsonPath("$.height").value(105))
                .andExpect(jsonPath("$.zindex").value(10));
    }

    @Test
    public void shouldReturnNotFoundWhenDeletingANonexistentWidget() throws Exception {
        mockMvc.perform(delete("/widget/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnWidgetInResponseBodyAfterDeletingIt() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";
        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(completeRequestBody));

        mockMvc.perform(delete("/widget/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(10))
                .andExpect(jsonPath("$.y").value(20))
                .andExpect(jsonPath("$.width").value(200))
                .andExpect(jsonPath("$.height").value(100))
                .andExpect(jsonPath("$.zindex").value(5));
    }

    @Test
    public void shouldReturnEmptyListIfNoWidgetsArePresent() throws Exception {
        mockMvc.perform(get("/widgets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void shouldReturnListOfWidgetsIfThereIsAtLeastOneWidget() throws Exception {
        String completeRequestBody = "{ \"x\": 10, \"y\": 20, \"width\": 200, \"height\": 100, \"zindex\": 5 }";
        mockMvc.perform(post("/widget").contentType(APPLICATION_JSON).content(completeRequestBody));

        mockMvc.perform(get("/widgets"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].x").value(10))
                .andExpect(jsonPath("$[0].y").value(20))
                .andExpect(jsonPath("$[0].width").value(200))
                .andExpect(jsonPath("$[0].height").value(100))
                .andExpect(jsonPath("$[0].zindex").value(5));
    }
}

package io.github.legionivo.plugin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
public class TestStep {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("steps")
    private List<TestStep> steps;

    public TestStep setName(String name) {
        this.name = name;
        return this;
    }
}

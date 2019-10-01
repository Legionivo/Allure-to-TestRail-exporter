package io.github.legionivo.plugin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CustomStepsSeparated {
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("expected")
    @Expose
    private String expected;
}

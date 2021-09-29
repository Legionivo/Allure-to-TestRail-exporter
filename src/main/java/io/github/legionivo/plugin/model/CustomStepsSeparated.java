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
    @SerializedName("additional_info")
    @Expose
    private String additionalInfo;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("expected")
    @Expose
    private String expected;
    @SerializedName("refs")
    @Expose
    private String refs;
}

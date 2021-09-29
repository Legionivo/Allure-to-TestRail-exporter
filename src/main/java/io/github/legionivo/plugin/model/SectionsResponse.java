package io.github.legionivo.plugin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class SectionsResponse {
    @SerializedName("offset")
    @Expose
    private Integer offset;

    @SerializedName("limit")
    @Expose
    private Integer limit;

    @SerializedName("size")
    @Expose
    private Integer size;

    @SerializedName("_links")
    @Expose
    private Object links;

    @SerializedName("sections")
    @Expose
    private List<Section> sections;
}

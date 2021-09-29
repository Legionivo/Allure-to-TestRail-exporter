package io.github.legionivo.plugin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Section {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("suite_id")
    @Expose
    private Integer suiteId;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("depth")
    @Expose
    private int depth;
    @SerializedName("display_order")
    @Expose
    private int displayOrder;

}

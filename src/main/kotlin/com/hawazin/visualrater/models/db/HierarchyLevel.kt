package com.hawazin.visualrater.models.db

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*

@Entity
data class HierarchyLevel (
    @Id
    @GeneratedValue(generator = "UUID") @GenericGenerator( name="UUID", strategy =  "org.hibernate.id.UUIDGenerator")
    var id: UUID?,
    var name:String,
    var score:Double,
    var thumbnail:String?,
    @Type(JsonType::class)
    var additionalProperties:Map<String,String>,
    var order:Int,
    @ManyToOne(optional = false)
    @JoinColumn(name = "childId")
    var parentLevel:HierarchyLevel,
    @OneToMany(cascade=[CascadeType.REMOVE])
    @OrderBy("number ASC")
    @JoinColumn(name = "parentId")
    var children:List<HierarchyLevel>
)
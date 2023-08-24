package com.hawazin.visualrater.models.db

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*


@Entity
data class Hierarchy (
    @Id
    @GeneratedValue(generator = "UUID") @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    var id: UUID?,
    var name:String,
    @OneToMany(cascade=[CascadeType.REMOVE])
    var levels:MutableList<HierarchyLevel>
)
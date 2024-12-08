package com.studybuddy.demostud.models.disciplines_package;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "sub_discipline")
public class SubDiscipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private DisciplineCategory category;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DisciplineCategory getCategory() {
        return category;
    }

    public void setCategory(DisciplineCategory category) {
        this.category = category;
    }


    @Override
    public String toString() {
        return "SubDiscipline{id=" + id + ", name='" + name + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubDiscipline that = (SubDiscipline) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package dev.adlin.llm.memory.entity;


import dev.adlin.llm.adapter.Role;
import jakarta.persistence.*;

import java.util.Date;

@Table(name = "long_term")
@Entity
public class LongTermMemoryEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "content")
    private String content;

    @Column(name = "username")
    private String username;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public LongTermMemoryEntity(Long id, Role role, String content, String username, Date date) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.username = username;
        this.date = date;
    }

    public LongTermMemoryEntity() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

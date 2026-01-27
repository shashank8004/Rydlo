package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="owners")
@Getter
@Setter
@AttributeOverride(name = "id",column = @Column(name="owner_id"))
@ToString(callSuper = true,exclude = "myUser")
public class Owner extends BaseEntity {
    
	@Column(name = "shop_name",length =30)
	private String shopName;
	
	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User myUser;
}

package me.night.helldev.member;

import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.persistence.document.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class Member extends Document {

    private String name;
    private ArrayList<String> warns;
    private ArrayList<String> bans;

    public Member() {
        this.warns = new ArrayList<>();
        this.bans = new ArrayList<>();
    }


    public String getIdAsString() {
        return this.getPath().getValue();
    }

}

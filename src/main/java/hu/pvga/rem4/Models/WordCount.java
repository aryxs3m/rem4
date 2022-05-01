/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * WordCounter Feature
 *
 * WordCount model
 */
@DatabaseTable(tableName = "wordcounts")
public class WordCount {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String word;

    @DatabaseField
    private Date since;

    @DatabaseField(defaultValue = "0")
    private int count;

    public WordCount() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Date getSince() {
        return since;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

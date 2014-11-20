/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.user.entity.embedded;

import com.creditcloud.common.entities.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * <p>认证对应的评估，可是多维度的衡量
 * <p>TODO 目前为了简化问题，认为只是对应到一个具体分数
 *
 * @author rooseek
 */
@Embeddable
public class Assessment extends BaseEntity {

    @Column(nullable = false)
    private int score;

    public Assessment(int score) {
        this.score = score;
    }

    public Assessment() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.score;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final Assessment other = (Assessment) obj;
        return score == other.score;
    }
}

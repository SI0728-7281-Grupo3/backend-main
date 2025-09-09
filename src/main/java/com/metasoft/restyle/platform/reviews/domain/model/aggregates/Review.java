package com.metasoft.restyle.platform.reviews.domain.model.aggregates;

import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ContractorId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ProjectId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.Rating;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Review extends AbstractAggregateRoot<Review> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Embedded
    private ContractorId contractorId;

    @Embedded
    private ProjectId projectId;

    @Getter
    private String duration;

    @Embedded
    private Rating rating;

    @Getter
    private String comment;

    @Getter //image url of the review
    private String image;

    public Review(Integer contractorId, Integer projectId, String duration, Integer rating, String comment, String image) {
        this.contractorId = new ContractorId(contractorId);
        this.projectId = new ProjectId(projectId);
        this.duration = duration;
        this.rating = new Rating(rating);
        this.comment = comment;
        this.image = image;
    }

    public Review() {
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateDuration(String duration) {
        this.duration = duration;
    }

    public Integer getContractorId() {
        return contractorId.getContractorId();
    }

    public Integer getProjectId() {
        return projectId.getProjectId();
    }

    public Integer getRating() {
        return rating.getRating();
    }
}

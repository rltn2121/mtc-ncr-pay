package core.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="cor_sda_main_mas")
@IdClass(SdaMainMasId.class)
public class SdaMainMas {

    @Id
    @Comment("고객번호")
    private String acno ;

    @Id
    @Comment("통화코드")
    private String cur_c ;

    @Comment("계좌잔액")
    private Double ac_jan;

}

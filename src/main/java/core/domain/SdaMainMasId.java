package core.domain;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class SdaMainMasId implements Serializable {
    @Column(name = "acno")
    private String acno ;

    @Column(name = "cur_c")
    private String cur_c ;
}

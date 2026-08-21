package com.webgis.evaluationform;

import com.webgis.map.finalmap.FinalMap;
import com.webgis.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationFormRepository extends JpaRepository<EvaluationForm,Integer> {
    Optional<EvaluationForm> findById(long id);

    List<EvaluationForm> findByFinalMap(FinalMap finalMap);
    List<EvaluationForm> findByFinalMapAndUser(FinalMap finalMap, User user);

    List<EvaluationForm>
    findByFinalMapAndDivisionAndPerceivedRiskIsNotNullAndCertaintyLevelIsNotNullAndIsPublicTrue(
            FinalMap
            finalMap, String division);

    List<EvaluationForm>
    findByFinalMapAndDivisionAndAgreementLevelIsNotNullAndIsPublicTrue(
            FinalMap finalMap,
            String division);

    List<EvaluationForm>
    findByFinalMapAndDivisionAndCertaintyLevelIsNotNullAndIsPublicTrue(
            FinalMap finalMap,
            String division);


    Optional<EvaluationForm> findByUserAndDivisionAndFinalMap(User user, String division, FinalMap finalMap);

    boolean existsByUserAndDivisionAndFinalMap(User user, String division, FinalMap finalMap);

    long countByFinalMapAndIsPublicTrue(FinalMap finalMap);

    long countByFinalMapAndDivisionAndIsPublicTrue(FinalMap finalMap, String division);

    //Manual Query has it is not possible directly with jpa function
    @Query("""
    SELECT DISTINCT e.division
    FROM EvaluationForm e
    WHERE e.isPublic = true
      AND e.finalMap = :map
      AND e.perceivedRisk IS NOT NULL
      AND e.certaintyLevel IS NOT NULL
    """)
    List<String> findDivisionsWithValidPublicEvaluationForms(@Param("map") FinalMap map);
}

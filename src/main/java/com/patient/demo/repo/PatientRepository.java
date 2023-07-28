package com.patient.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.patient.demo.entity.PatientEntity;
import com.patient.demo.entity.SearchSummary;

@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, String>,JpaSpecificationExecutor<PatientEntity> {
	/*
	 *	2023-07-27 
	 *	1.함수를 하나로 합칠수있는지?
	 *  넘겨받은 값에 따라서..
	 *  
	 */
	
	List<PatientEntity> findByName(String name);
	
	@Query(value = 
			"select a.name, a.age, a.gender, a.disease, b.path " + 
			"from patient_info a join patient_images b " + 
			"on a.seq = b.patient_seq " + 
			"where a.delete_flag = '미삭제' " + 
			"and b.path != '' " + 
			"order by a.seq asc", nativeQuery = true)
	List<SearchSummary> findAllList();
	
	@Query(value = 
			"select a.name, a.age, a.gender, a.disease, b.path " + 
			"from patient_info a join patient_images b " + 
			"on a.seq = b.patient_seq " + 
			"where a.delete_flag = '미삭제' " + 
			"and b.path != '' " +
			"and a.name = :name " +
			"order by a.seq asc ", nativeQuery = true)
	List<SearchSummary> findSearchName(@Param(value = "name") String name);
}

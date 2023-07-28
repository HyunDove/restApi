package com.patient.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.patient.demo.entity.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
	@Query(value = 
			"select b.* " + 
			"from patient_info a right join patient_images b " + 
			"on a.seq = b.patient_seq " + 
			"where a.name = :name " + 
			"and b.delete_flag = '미삭제' "
			, nativeQuery = true)
	ImageEntity findByName(@Param("name") String name);
}

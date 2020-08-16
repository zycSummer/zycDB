package com.zyc.db.repository;

import com.zyc.db.entity.ZycAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: zyc
 * Date: 2020/8/15
 * Time: 20:30
 * Description:
 */
@Repository
public interface ZycAllRepo extends JpaRepository<ZycAll, Integer>, QuerydslPredicateExecutor<ZycAll> {
}

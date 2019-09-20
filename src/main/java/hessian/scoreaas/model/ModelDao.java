package hessian.scoreaas.model;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface ModelDao {
    // Save
    @Insert
    public Model save(Model model);

    // Delete
    @Delete(entityClass = Model.class)
    public void delete(String model_name, Integer model_version);

    @Delete(entityClass = Model.class)
    public void deleteAll(String model_name);

    // Select
    @Select
    public Model findByModelNameAndModelVersion(String model_name, Integer model_version);

    @Select
    public PagingIterable<Model> findByModelName(String model_name);

    @Select
    public PagingIterable<Model> findAll();
}

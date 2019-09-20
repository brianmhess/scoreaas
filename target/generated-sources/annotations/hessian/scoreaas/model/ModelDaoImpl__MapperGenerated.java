package hessian.scoreaas.model;

import com.datastax.dse.driver.internal.mapper.DseDaoBase;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy;
import com.datastax.oss.driver.internal.core.util.concurrent.BlockingOperation;
import com.datastax.oss.driver.internal.core.util.concurrent.CompletableFutures;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.Throwable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generated by the DataStax driver mapper, do not edit directly.
 */
public class ModelDaoImpl__MapperGenerated extends DseDaoBase implements ModelDao {
  private static final Logger LOG = LoggerFactory.getLogger(ModelDaoImpl__MapperGenerated.class);

  private final ModelHelper__MapperGenerated modelHelper;

  private final PreparedStatement saveStatement;

  private final PreparedStatement deleteStatement;

  private final PreparedStatement deleteAllStatement;

  private final PreparedStatement findByModelNameAndModelVersionStatement;

  private final PreparedStatement findByModelNameStatement;

  private final PreparedStatement findAllStatement;

  private ModelDaoImpl__MapperGenerated(MapperContext context,
      ModelHelper__MapperGenerated modelHelper, PreparedStatement saveStatement,
      PreparedStatement deleteStatement, PreparedStatement deleteAllStatement,
      PreparedStatement findByModelNameAndModelVersionStatement,
      PreparedStatement findByModelNameStatement, PreparedStatement findAllStatement) {
    super(context);
    this.modelHelper = modelHelper;
    this.saveStatement = saveStatement;
    this.deleteStatement = deleteStatement;
    this.deleteAllStatement = deleteAllStatement;
    this.findByModelNameAndModelVersionStatement = findByModelNameAndModelVersionStatement;
    this.findByModelNameStatement = findByModelNameStatement;
    this.findAllStatement = findAllStatement;
  }

  @Override
  public Model save(Model model) {
    BoundStatementBuilder boundStatementBuilder = saveStatement.boundStatementBuilder();
    modelHelper.set(model, boundStatementBuilder, NullSavingStrategy.DO_NOT_SET);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, modelHelper);
  }

  @Override
  public void delete(String model_name, Integer model_version) {
    BoundStatementBuilder boundStatementBuilder = deleteStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("model_name", model_name, String.class);

    boundStatementBuilder = boundStatementBuilder.set("model_version", model_version, Integer.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  @Override
  public void deleteAll(String model_name) {
    BoundStatementBuilder boundStatementBuilder = deleteAllStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("model_name", model_name, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    execute(boundStatement);
  }

  @Override
  public Model findByModelNameAndModelVersion(String model_name, Integer model_version) {
    BoundStatementBuilder boundStatementBuilder = findByModelNameAndModelVersionStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("model_name", model_name, String.class);

    boundStatementBuilder = boundStatementBuilder.set("model_version", model_version, Integer.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToSingleEntity(boundStatement, modelHelper);
  }

  @Override
  public PagingIterable<Model> findByModelName(String model_name) {
    BoundStatementBuilder boundStatementBuilder = findByModelNameStatement.boundStatementBuilder();

    boundStatementBuilder = boundStatementBuilder.set("model_name", model_name, String.class);

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToEntityIterable(boundStatement, modelHelper);
  }

  @Override
  public PagingIterable<Model> findAll() {
    BoundStatementBuilder boundStatementBuilder = findAllStatement.boundStatementBuilder();

    BoundStatement boundStatement = boundStatementBuilder.build();
    return executeAndMapToEntityIterable(boundStatement, modelHelper);
  }

  public static CompletableFuture<ModelDao> initAsync(MapperContext context) {
    LOG.debug("[{}] Initializing new instance for keyspace = {} and table = {}",
        context.getSession().getName(),
        context.getKeyspaceId(),
        context.getTableId());
    throwIfProtocolVersionV3(context);
    try {
      // Initialize all entity helpers
      ModelHelper__MapperGenerated modelHelper = new ModelHelper__MapperGenerated(context);
      List<CompletionStage<PreparedStatement>> prepareStages = new ArrayList<>();
      // Prepare the statement for `save(hessian.scoreaas.model.Model)`:
      SimpleStatement saveStatement_simple = modelHelper.insert().build();
      LOG.debug("[{}] Preparing query `{}` for method save(hessian.scoreaas.model.Model)",
          context.getSession().getName(),
          saveStatement_simple.getQuery());
      CompletionStage<PreparedStatement> saveStatement = prepare(saveStatement_simple, context);
      prepareStages.add(saveStatement);
      // Prepare the statement for `delete(java.lang.String,java.lang.Integer)`:
      SimpleStatement deleteStatement_simple = modelHelper.deleteByPrimaryKeyParts(2).build();
      LOG.debug("[{}] Preparing query `{}` for method delete(java.lang.String,java.lang.Integer)",
          context.getSession().getName(),
          deleteStatement_simple.getQuery());
      CompletionStage<PreparedStatement> deleteStatement = prepare(deleteStatement_simple, context);
      prepareStages.add(deleteStatement);
      // Prepare the statement for `deleteAll(java.lang.String)`:
      SimpleStatement deleteAllStatement_simple = modelHelper.deleteByPrimaryKeyParts(1).build();
      LOG.debug("[{}] Preparing query `{}` for method deleteAll(java.lang.String)",
          context.getSession().getName(),
          deleteAllStatement_simple.getQuery());
      CompletionStage<PreparedStatement> deleteAllStatement = prepare(deleteAllStatement_simple, context);
      prepareStages.add(deleteAllStatement);
      // Prepare the statement for `findByModelNameAndModelVersion(java.lang.String,java.lang.Integer)`:
      SimpleStatement findByModelNameAndModelVersionStatement_simple = modelHelper.selectByPrimaryKeyParts(2).build();
      LOG.debug("[{}] Preparing query `{}` for method findByModelNameAndModelVersion(java.lang.String,java.lang.Integer)",
          context.getSession().getName(),
          findByModelNameAndModelVersionStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findByModelNameAndModelVersionStatement = prepare(findByModelNameAndModelVersionStatement_simple, context);
      prepareStages.add(findByModelNameAndModelVersionStatement);
      // Prepare the statement for `findByModelName(java.lang.String)`:
      SimpleStatement findByModelNameStatement_simple = modelHelper.selectByPrimaryKeyParts(1).build();
      LOG.debug("[{}] Preparing query `{}` for method findByModelName(java.lang.String)",
          context.getSession().getName(),
          findByModelNameStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findByModelNameStatement = prepare(findByModelNameStatement_simple, context);
      prepareStages.add(findByModelNameStatement);
      // Prepare the statement for `findAll()`:
      SimpleStatement findAllStatement_simple = modelHelper.selectByPrimaryKeyParts(0).build();
      LOG.debug("[{}] Preparing query `{}` for method findAll()",
          context.getSession().getName(),
          findAllStatement_simple.getQuery());
      CompletionStage<PreparedStatement> findAllStatement = prepare(findAllStatement_simple, context);
      prepareStages.add(findAllStatement);
      // Initialize all method invokers
      // Build the DAO when all statements are prepared
      return CompletableFutures.allSuccessful(prepareStages)
          .thenApply(v -> (ModelDao) new ModelDaoImpl__MapperGenerated(context,
              modelHelper,
              CompletableFutures.getCompleted(saveStatement),
              CompletableFutures.getCompleted(deleteStatement),
              CompletableFutures.getCompleted(deleteAllStatement),
              CompletableFutures.getCompleted(findByModelNameAndModelVersionStatement),
              CompletableFutures.getCompleted(findByModelNameStatement),
              CompletableFutures.getCompleted(findAllStatement)))
          .toCompletableFuture();
    } catch (Throwable t) {
      return CompletableFutures.failedFuture(t);
    }
  }

  public static ModelDao init(MapperContext context) {
    BlockingOperation.checkNotDriverThread();
    return CompletableFutures.getUninterruptibly(initAsync(context));
  }
}
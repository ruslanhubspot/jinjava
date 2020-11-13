package com.hubspot.jinjava.el.ext.eager;

import com.hubspot.jinjava.el.ext.DeferredParsingException;
import com.hubspot.jinjava.util.ChunkResolver;
import de.odysseus.el.tree.Bindings;
import de.odysseus.el.tree.impl.ast.AstDot;
import de.odysseus.el.tree.impl.ast.AstMethod;
import de.odysseus.el.tree.impl.ast.AstParameters;
import de.odysseus.el.tree.impl.ast.AstProperty;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.el.ELContext;

public class EagerAstMethodDecorator extends AstMethod implements EvalResultHolder {
  private Object evalResult;
  // instanceof AstProperty
  protected final EvalResultHolder property;
  // instanceof AstParameters
  protected final EvalResultHolder params;

  public EagerAstMethodDecorator(AstProperty property, AstParameters params) {
    this(
      EagerAstNodeDecorator.getAsEvalResultHolder(property),
      EagerAstParametersDecorator.getAsEvalResultHolder(params)
    );
  }

  private EagerAstMethodDecorator(EvalResultHolder property, EvalResultHolder params) {
    super((AstProperty) property, (AstParameters) params);
    this.property = property;
    this.params = params;
  }

  @Override
  public Object eval(Bindings bindings, ELContext context) {
    try {
      evalResult = super.eval(bindings, context);
      return evalResult;
    } catch (DeferredParsingException e) {
      StringBuilder sb = new StringBuilder();

      String paramString;
      // params get evaluated first
      if (params.hasEvalResult()) {
        paramString =
          ChunkResolver.getValueAsJinjavaStringSafe(params.getAndClearEvalResult());
      } else {
        paramString = e.getDeferredEvalResult();
        e = null;
      }
      String propertyString = "";
      if (property.hasEvalResult()) {
        propertyString =
          ChunkResolver.getValueAsJinjavaStringSafe(property.getAndClearEvalResult());
      } else if (e != null) {
        if (property instanceof EagerAstDotDecorator) {
          propertyString =
            String.format(
              "%s.%s",
              e.getDeferredEvalResult(),
              ((EagerAstDotDecorator) property).getProperty()
            );
        } else {
          propertyString = e.getDeferredEvalResult();
        }
      } else {
        try {
          ((AstProperty) property).appendStructure(sb, bindings);
        } catch (DeferredParsingException e1) {
          propertyString = e1.getDeferredEvalResult();
        }
      }
      sb.append(propertyString);
      sb.append(String.format("(%s)", paramString));
      throw new DeferredParsingException(sb.toString());
    } finally {
      property.getAndClearEvalResult();
      params.getAndClearEvalResult();
    }
  }

  @Override
  public Object getAndClearEvalResult() {
    Object temp = evalResult;
    evalResult = null;
    return temp;
  }

  @Override
  public boolean hasEvalResult() {
    return evalResult != null;
  }
}

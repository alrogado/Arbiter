package org.deeplearning4j.arbiter.layers;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.FixedValue;
import org.deeplearning4j.arbiter.util.CollectionUtils;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;


import java.util.ArrayList;
import java.util.List;

/**
 * Layer space for a {@link GlobalPoolingLayer}
 *
 * @author Alex Black
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE) //For Jackson JSON/YAML deserialization
public class GlobalPoolingLayerSpace extends LayerSpace<GlobalPoolingLayer> {

    protected ParameterSpace<int[]> poolingDimensions;
    protected ParameterSpace<Boolean> collapseDimensions;
    protected ParameterSpace<PoolingType> poolingType;
    protected ParameterSpace<Integer> pNorm;

    private int numParameters;

    private GlobalPoolingLayerSpace(Builder builder){
        super(builder);
        this.poolingDimensions = builder.poolingDimensions;
        this.collapseDimensions = builder.collapseDimensions;
        this.poolingType = builder.poolingType;
        this.pNorm = builder.pNorm;

        this.numParameters = CollectionUtils.countUnique(collectLeaves());
    }

    @Override
    public GlobalPoolingLayer getValue(double[] parameterValues) {
        GlobalPoolingLayer.Builder builder = new GlobalPoolingLayer.Builder();
        super.setLayerOptionsBuilder(builder, parameterValues);
        if(poolingDimensions != null) builder.poolingDimensions(poolingDimensions.getValue(parameterValues));
        if(collapseDimensions != null) builder.collapseDimensions(collapseDimensions.getValue(parameterValues));
        if(poolingType != null) builder.poolingType(poolingType.getValue(parameterValues));
        if(pNorm != null) builder.pnorm(pNorm.getValue(parameterValues));
        return builder.build();
    }

    @Override
    public int numParameters() {
        return numParameters;
    }

    @Override
    public List<ParameterSpace> collectLeaves() {
        List<ParameterSpace> out = super.collectLeaves();
        if(poolingDimensions != null) out.addAll(poolingDimensions.collectLeaves());
        if(collapseDimensions != null) out.addAll(collapseDimensions.collectLeaves());
        if(poolingType != null) out.addAll(poolingType.collectLeaves());
        if(pNorm != null) out.addAll(pNorm.collectLeaves());
        return out;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void setIndices(int... indices) {
        throw new UnsupportedOperationException("Cannot set indices for non-leaf parameter space");
    }



    public static class Builder extends LayerSpace.Builder<Builder> {

        protected ParameterSpace<int[]> poolingDimensions;
        protected ParameterSpace<Boolean> collapseDimensions;
        protected ParameterSpace<PoolingType> poolingType;
        protected ParameterSpace<Integer> pNorm;

        public Builder poolingDimensions(int... poolingDimensions ){
            return poolingDimensions(new FixedValue<>(poolingDimensions));
        }

        public Builder poolingDimensions(ParameterSpace<int[]> poolingDimensions){
            this.poolingDimensions = poolingDimensions;
            return this;
        }

        public Builder collapseDimensions(boolean collapseDimensions){
            return collapseDimensions(new FixedValue<>(collapseDimensions));
        }

        public Builder collapseDimensions(ParameterSpace<Boolean> collapseDimensions){
            this.collapseDimensions = collapseDimensions;
            return this;
        }

        public Builder poolingType(PoolingType poolingType){
            return poolingType(new FixedValue<>(poolingType));
        }

        public Builder poolingType(ParameterSpace<PoolingType> poolingType){
            this.poolingType = poolingType;
            return this;
        }

        public Builder pNorm(int pNorm){
            return pNorm(new FixedValue<>(pNorm));
        }

        public Builder pNorm(ParameterSpace<Integer> pNorm){
            this.pNorm = pNorm;
            return this;
        }

        public GlobalPoolingLayerSpace build(){
            return new GlobalPoolingLayerSpace(this);
        }
    }
}

/*******************************************************************************
 * Copyright (c) 2010-2019 Haifeng Li
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package smile.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import smile.data.type.*;
import smile.data.vector.*;
import smile.math.matrix.DenseMatrix;

/**
 * A data frame with a new index instead of the default [0, n) row index.
 *
 * @author Haifeng Li
 */
class IndexDataFrame implements DataFrame {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IndexDataFrame.class);

    /** The underlying data frame. */
    private DataFrame df;
    /** The row index. */
    private int[] index;

    /**
     * Constructor.
     * @param df The underlying data frame.
     * @param index The row index.
     */
    public IndexDataFrame(DataFrame df, int[] index) {
        this.df = df;
        this.index = index;
    }

    @Override
    public StructType schema() {
        return df.schema();
    }

    @Override
    public String toString() {
        return toString(10, true);
    }

    @Override
    public Iterator<BaseVector> iterator() {
        return df.iterator();
    }

    @Override
    public int columnIndex(String name) {
        return df.columnIndex(name);
    }

    @Override
    public int size() {
        return df.size();
    }

    @Override
    public int ncols() {
        return df.ncols();
    }

    @Override
    public Object get(int i, int j) {
        return df.get(index[i], j);
    }

    @Override
    public Stream<Tuple> stream() {
        return Arrays.stream(index).mapToObj(i -> df.get(i));
    }

    @Override
    public BaseVector column(int i) {
        return df.column(i);
    }

    @Override
    public <T> Vector<T> vector(int i) {
        return df.vector(i);
    }

    @Override
    public BooleanVector booleanVector(int i) {
        return df.booleanVector(i);
    }

    @Override
    public CharVector charVector(int i) {
        return df.charVector(i);
    }

    @Override
    public ByteVector byteVector(int i) {
        return df.byteVector(i);
    }

    @Override
    public ShortVector shortVector(int i) {
        return df.shortVector(i);
    }

    @Override
    public IntVector intVector(int i) {
        return df.intVector(i);
    }

    @Override
    public LongVector longVector(int i) {
        return df.longVector(i);
    }

    @Override
    public FloatVector floatVector(int i) {
        return df.floatVector(i);
    }

    @Override
    public DoubleVector doubleVector(int i) {
        return df.doubleVector(i);
    }

    @Override
    public DataFrame select(int... cols) {
        return new IndexDataFrame(df.select(cols), index);
    }

    @Override
    public DataFrame drop(int... cols) {
        return new IndexDataFrame(df.drop(cols), index);
    }

    /** Returns a new data frame with regular index. */
    private DataFrame rebase() {
        return DataFrame.of(stream().collect(Collectors.toList()));
    }

    @Override
    public DataFrame merge(DataFrame... dataframes) {
        for (DataFrame df : dataframes) {
            if (df.size() != size()) {
                throw new IllegalArgumentException("Merge data frames with different size: " + size() + " vs " + df.size());
            }
        }

        return rebase().merge(dataframes);
    }

    @Override
    public DataFrame merge(BaseVector... vectors) {
        for (BaseVector vector : vectors) {
            if (vector.size() != size()) {
                throw new IllegalArgumentException("Merge data frames with different size: " + size() + " vs " + vector.size());
            }
        }

        return rebase().merge(vectors);
    }

    @Override
    public DataFrame union(DataFrame... dataframes) {
        for (DataFrame df : dataframes) {
            if (!schema().equals(df.schema())) {
                throw new IllegalArgumentException("Union data frames with different schema: " + schema() + " vs " + df.schema());
            }
        }

        return rebase().union(dataframes);
    }

    @Override
    public Tuple get(int i) {
        return df.get(index[i]);
    }

    @Override
    public DenseMatrix toMatrix() {
        // Although clean, this is not optimal in term of performance.
        return rebase().toMatrix();
    }
}
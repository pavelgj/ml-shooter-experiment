/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.stat;

import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.moment.Variance;

/**
 * StatUtils provides static methods for computing statistics based on data
 * stored in double[] arrays.
 *
 * @version $Revision: 1073276 $ $Date: 2011-02-22 10:34:52 +0100 (mar. 22 févr. 2011) $
 */
public final class StatUtils {

    /** mean */
    private static final UnivariateStatistic MEAN = new Mean();

    /** variance */
    private static final Variance VARIANCE = new Variance();

    /**
     * Private Constructor
     */
    private StatUtils() {
    }

    /**
     * Returns the arithmetic mean of the entries in the input array, or
     * <code>Double.NaN</code> if the array is empty.
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     * <p>
     * See {@link org.apache.commons.math.stat.descriptive.moment.Mean} for
     * details on the computing algorithm.</p>
     *
     * @param values the input array
     * @return the mean of the values or Double.NaN if the array is empty
     * @throws IllegalArgumentException if the array is null
     */
    public static double mean(final double[] values) {
        return MEAN.evaluate(values);
    }
    
    /**
     * Returns the variance of the entries in the input array, using the
     * precomputed mean value.  Returns <code>Double.NaN</code> if the array
     * is empty.
     * <p>
     * See {@link org.apache.commons.math.stat.descriptive.moment.Variance} for
     * details on the computing algorithm.</p>
     * <p>
     * The formula used assumes that the supplied mean value is the arithmetic
     * mean of the sample data, not a known population parameter.  This method
     * is supplied only to save computation when the mean has already been
     * computed.</p>
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.</p>
     * <p>
     * Throws <code>IllegalArgumentException</code> if the array is null.</p>
     *
     * @param values the input array
     * @param mean the precomputed mean value
     * @return the variance of the values or Double.NaN if the array is empty
     * @throws IllegalArgumentException if the array is null
     */
    public static double variance(final double[] values, final double mean) {
        return VARIANCE.evaluate(values, mean);
    }
}

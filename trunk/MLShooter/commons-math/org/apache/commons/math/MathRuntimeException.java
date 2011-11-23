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
package org.apache.commons.math;

import java.text.ParseException;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import org.apache.commons.math.exception.MathThrowable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

import com.experiment.mlshooter.client.Utils;

/**
* Base class for commons-math unchecked exceptions.
*
* @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
* @since 2.0
*/
public class MathRuntimeException extends RuntimeException implements MathThrowable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 9058794795027570002L;

    /**
     * Pattern used to build the message.
     */
    private final Localizable pattern;

    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public MathRuntimeException(final Localizable pattern, final Object ... arguments) {
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : Utils.clone(arguments);
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * nested <code>Throwable</code> root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public MathRuntimeException(final Throwable rootCause) {
        super(rootCause);
        this.pattern   = LocalizedFormats.SIMPLE_MESSAGE;
        this.arguments = new Object[] { (rootCause == null) ? "" : rootCause.getMessage() };
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message and nested <code>Throwable</code> root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @param pattern format specifier
     * @param arguments format arguments
     * @since 2.2
     */
    public MathRuntimeException(final Throwable rootCause,
                                final Localizable pattern, final Object ... arguments) {
        super(rootCause);
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : Utils.clone(arguments);
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.2
     */
    public Localizable getSpecificPattern() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.2
     */
    public Localizable getGeneralPattern() {
        return pattern;
    }

    /** {@inheritDoc} */
    public Object[] getArguments() {
        return Utils.clone(arguments);
    }

    /**
     * Prints the stack trace of this exception to the standard error stream.
     */
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Constructs a new <code>ArithmeticException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ArithmeticException createArithmeticException(final Localizable pattern,
                                                                final Object ... arguments) {
        return new ArithmeticException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 5305498554076846637L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>ArrayIndexOutOfBoundsException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(final Localizable pattern,
                                                                                      final Object ... arguments) {
        return new ArrayIndexOutOfBoundsException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6718518191249632175L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>IllegalArgumentException</code> with specified nested
     * <code>Throwable</code> root cause.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @return built exception
     */
    public static IllegalArgumentException createIllegalArgumentException(final Throwable rootCause) {
        IllegalArgumentException iae = new IllegalArgumentException(rootCause.getLocalizedMessage());
        iae.initCause(rootCause);
        return iae;
    }

    /**
     * Constructs a new <code>IllegalStateException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static IllegalStateException createIllegalStateException(final Localizable pattern,
                                                                    final Object ... arguments) {
        return new IllegalStateException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 6880901520234515725L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>ConcurrentModificationException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ConcurrentModificationException createConcurrentModificationException(final Localizable pattern,
                                                                                        final Object ... arguments) {
        return new ConcurrentModificationException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -1878427236170442052L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>NoSuchElementException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static NoSuchElementException createNoSuchElementException(final Localizable pattern,
                                                                      final Object ... arguments) {
        return new NoSuchElementException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 1632410088350355086L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>UnsupportedOperationException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     * @deprecated in 2.2. Please use {@link org.apache.commons.math.exception.MathUnsupportedOperationException}
     * instead.
     */
    @Deprecated
    public static UnsupportedOperationException createUnsupportedOperationException(final Localizable pattern,
                                                                                    final Object ... arguments) {
        return new UnsupportedOperationException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -4284649691002411505L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>NullPointerException</code> with specified formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     * @deprecated in 2.2. Checks for "null" must not be performed in Commons-Math.
     */
    @Deprecated
    public static NullPointerException createNullPointerException(final Localizable pattern,
                                                                  final Object ... arguments) {
        return new NullPointerException() {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 451965530686593945L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /**
     * Constructs a new <code>ParseException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param offset offset at which error occurred
     * @param pattern format specifier
     * @param arguments format arguments
     * @return built exception
     * @since 2.2
     */
    public static ParseException createParseException(final int offset,
                                                      final Localizable pattern,
                                                      final Object ... arguments) {
        return new ParseException(null, offset) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = 8153587599409010120L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };
    }

    /** Create an {@link java.lang.RuntimeException} for an internal error.
     * @param cause underlying cause
     * @return an {@link java.lang.RuntimeException} for an internal error
     */
    public static RuntimeException createInternalError(final Throwable cause) {

        final String argument = "https://issues.apache.org/jira/browse/MATH";

        return new RuntimeException(cause) {

            /** Serializable version identifier. */
            private static final long serialVersionUID = -201865440834027016L;

            /** {@inheritDoc} */
            @Override
            public String getMessage() {
                return "yo";
            }

            /** {@inheritDoc} */
            @Override
            public String getLocalizedMessage() {
                return "yo";
            }

        };

    }

}

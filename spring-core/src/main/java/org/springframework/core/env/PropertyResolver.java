/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.env;

import org.springframework.lang.Nullable;

/**
 * Interface for resolving properties against any underlying source.
 * roboslyq -->属性解析，主要具有两个功能：
 * 	(1)通过propertyName属性名获取与之对应的propertValue属性值（getProperty）。
 * 	(2)把${propertyName:defaultValue}格式的属性占位符，替换为实际的值(resolvePlaceholders)。
 *  注意：getProperty获取的属性值，全都是调用resolvePlaceholders进行占位符替换后的值。
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

	/**
	 * Return whether the given property key is available for resolution,
	 * i.e. if the value for the given key is not {@code null}.
	 * roboslyq --> 返回是否包含指定的property key（是否包含是指指定key的value不为null）
	 */
	boolean containsProperty(String key);

	/**
	 * Return the property value associated with the given key,
	 * or {@code null} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 * roboslyq --> 根据给定的KEY，返回具体的Value，如果没有找到value，则返回null
	 */
	@Nullable
	String getProperty(String key);

	/**
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 * roboslyq --> 根据给定的KEY，返回具体的Value，如果没有找到value，则返回默认值defaultValue
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * Return the property value associated with the given key,
	 * or {@code null} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @see #getRequiredProperty(String, Class)
	 * roboslyq --> 根据给定的KEY，返回具体的Value，如果没有找到value，则返回null。
	 * 并且强制指定返回具体的value类型为targetType（例如上面的方法为String）
	 */
	@Nullable
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * Return the property value associated with the given key,
	 * or {@code defaultValue} if the key cannot be resolved.
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String, Class)
	 * 	roboslyq -->  根据给定的KEY，返回具体的Value，如果没有找到value，则返回defaultValue。
	 * 	 并且强制指定返回具体的value类型为targetType（例如上面的方法为String）
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * Return the property value associated with the given key (never {@code null}).
	 * @throws IllegalStateException if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 * roboslyq --> 根据给定的KEY，返回具体的Value，如果没有找到则抛出异常
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * Return the property value associated with the given key, converted to the given
	 * targetType (never {@code null}).
	 * @throws IllegalStateException if the given key cannot be resolved
	 * roboslyq --> 根据给定的KEY，返回具体的Value，如果没有找到则抛出异常
	 * 		并且强制指定返回具体的value类型为targetType（例如上面的方法为String）
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value are ignored and passed through unchanged.
	 * @param text the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String)
	 * 解析${...}这种形式的占位符，具体内容使用getProperty方法返回的值填充。
	 * 对于未解析的占位符则原样返回
	 */
	String resolvePlaceholders(String text);

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value will cause an IllegalArgumentException to be thrown.
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * or if any placeholders are unresolvable
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String, boolean)
	 *  * 解析${...}这种形式的占位符，具体内容使用getProperty方法返回的值填充。
	 * 	 * 对于未解析的占位符则抛出异常
	 * 	 text = filePath
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}

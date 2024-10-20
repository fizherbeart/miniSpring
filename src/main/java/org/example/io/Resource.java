package org.example.io;

public record Resource(String path, String name) {
// Java 14 新特性, 用于创建不可变的数据载体类。
// 构造函数、equals、hashCode 和 toString 方法，以及所有组件的访问器方法。
}
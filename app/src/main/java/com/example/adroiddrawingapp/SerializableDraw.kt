package com.example.adroiddrawingapp

// Represents a single point in a path
data class PathPoint(val x: Float, val y: Float)

// Serializable version of a DrawPath
data class SerializablePath(val points: List<PathPoint>, val color: Int)
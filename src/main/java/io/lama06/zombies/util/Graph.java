package io.lama06.zombies.util;

import org.bukkit.Location;

import java.util.Vector;

public class Graph {
    public int size;
    public GraphPoint[] points;
    public double[][] distance;
    public double[][] finalDistance;
    public int[][] direction;
    public int[] ids;

    public Graph() {
        size = 0;
        points = new GraphPoint[1000];
        distance = new double[1000][1000];
        finalDistance = new double[1000][1000];
        direction = new int[1000][1000];
        ids = new int[1000];
    }

    public String show() {
        String s = "";
        for (int k = 0; k < size; ++k) {
            final int i = ids[k];
            s = s + "\n" + points[i].id + " ";
            for (GraphLink link : points[i].links) {
                s = s + link.id + " ";
            }
        }
        s += "\n";

        for (int k = 0; k < size; ++k) {
            final int i = ids[k];
            s += "\n";
            for (int t = 0; t < size; ++t) {
                final int j = ids[t];
                s += " " + finalDistance[i][j];
            }
        }

        for (int k = 0; k < size; ++k) {
            final int i = ids[k];
            s += "\n";
            for (int t = 0; t < size; ++t) {
                final int j = ids[t];
                s += " " + direction[i][j];
            }
        }
        s += "\n";

        return s;
    }

    public void addPoint(final GraphPoint point) {
        points[point.id] = point;
        int j;
        for (int i = 0; i < point.links.size(); ++i) {
            j = point.links.get(i).id;
            final double dist = point.location.distance(points[j].location);
            points[j].links.add(new GraphLink(point.id));
            if (points[j] != null) {
                distance[j][point.id] = distance[point.id][j] = dist;
            }
        }
        ids[size] = point.id;
        size++;
    }

    public void addLink(final int a, final int b) {
        final double dist = points[a].location.distance(points[b].location);
        points[a].links.add(new GraphLink(b));
        points[b].links.add(new GraphLink(a));
        distance[a][b] = distance[b][a] = dist;
    }

    public void calcFinalDistance() {

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (i == j) {
                    finalDistance[ids[i]][ids[j]] = 0;
                } else {
                    finalDistance[ids[i]][ids[j]] = (distance[ids[i]][ids[j]] == 0 ? 1000000 : distance[ids[i]][ids[j]]);
                }
            }
        }

        double t;
        for (int k = 0; k < size; ++k) {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    t = finalDistance[ids[i]][ids[k]] + finalDistance[ids[k]][ids[j]];
                    if (finalDistance[ids[i]][ids[j]] > t) {
                        finalDistance[ids[i]][ids[j]] = t;

                    }
                }
            }
        }

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {

                double min_dist = finalDistance[ids[i]][ids[j]];
                int result = ids[i];
                for (final GraphLink k : points[result].links) {
                    if (min_dist > finalDistance[k.id][ids[j]]) {
                        min_dist = finalDistance[k.id][ids[j]];
                        result = k.id;
                    }
                }
                direction[ids[i]][ids[j]] = result;


            }
        }

    }

    public int tracePath(final int index_a, final int index_b, final int step) {
        if (size == 0) {
            return -1;
        }

        int result = index_a;
        for (int i = 0; i < step; ++i) {
            result = direction[result][index_b];
        }
        return result;

    }

    public int findClosestPointIndex(final Location location) {
        if (size == 0) {
            return -1;
        }

        int result = 0;
        double min_dist = 1000000;
        double t;
        for (int i = 0; i < size; ++i) {
            t = location.distance(points[ids[i]].location);
            if (t < min_dist) {
                result = ids[i];
                min_dist = t;
            }
        }
        return result;
    }

    public int updateIndex(final Location location, final int id) {
        if (size == 0) {
            return -1;
        }

        int result = id;
        double min_dist = points[id].location.distance(location);
        double t;
        for (final GraphLink link : points[id].links) {
            t = location.distance(points[link.id].location);
            if (t < min_dist) {
                result = link.id;
                min_dist = t;
            }
        }
        return result;
    }

    public Location getPointLocation(final int index) {
        return points[index].location;
    }
}



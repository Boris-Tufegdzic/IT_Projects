#!/usr/bin/env python3
"""
compute sizes of all connected components.
sort and display.
"""

from sys import argv
import sys

from geo.point import Point




def print_components_sizes_file_dict(distance, points):
    """Renvoie les tailles des parties connexes avec un parcours en largeur"""
    n = len(points)
    E = {}
    for index_pts in range(n):
        for j in range(index_pts + 1, n):
            if points[index_pts].distance_to(points[j]) <= distance:
                if index_pts in E:
                    E[index_pts].append(j)
                else:
                    E[index_pts] = [j]
                if j in E:
                    E[j].append(index_pts)
                else:
                    E[j] = [index_pts]
    tailles_parties_connexes = []
    vus = set()
    def enumere_partie_connexe(index_point):
        a_voir = deque()
        a_voir.append(index_point)
        taille = 0

        while a_voir:
            curr = a_voir.popleft()
            if curr not in vus:
                vus.add(curr)
                taille += 1
                for index_voisin in E[curr]:
                        a_voir.append(index_voisin)

        return taille

    for index_point in range(n):
        if index_point not in vus:
            if index_point in E:
                taille = enumere_partie_connexe(index_point)
                tailles_parties_connexes.append(taille)
            else:
                tailles_parties_connexes.append(1)

    tailles_parties_connexes.sort(reverse=True)
    print(tailles_parties_connexes)
    
def print_components_sizes_file_abr(distance, points):
    """Renvoie les tailles des parties connexes avec un parcours en largeur"""
    n = len(points)
    E = {}
    abr = ABR(None)
    for i in range(n):
        abr.insert(points, i)
    for i in range(n):
        E[i] = abr.rechercher_voisins(points, i, distance)

    tailles_parties_connexes = []
    vus = set()
    def enumere_partie_connexe(index_point):
        a_voir = deque()
        a_voir.append(index_point)
        taille = 0

        while a_voir:
            curr = a_voir.popleft()
            if curr not in vus:
                vus.add(curr)
                taille += 1
                for index_voisin in E[curr]:
                    a_voir.append(index_voisin)

        return taille

    for index_point in range(n):
        if index_point not in vus:
            taille = enumere_partie_connexe(index_point)
            tailles_parties_connexes.append(taille)
    #tri et affichage des tailles
    tailles_parties_connexes.sort(reverse=True)
    print(tailles_parties_connexes)

def print_components_sizes_rec_abr(distance, points):
    """Renvoie les tailles des parties connexes avec un parcours en profondeur"""
    #Recherche des voisins avec un ABR trié selon les abscisses
    n = len(points)
    E = {}
    abr = ABR(None)
    for i in range(n):
        abr.insert(points, i)
    for i in range(n):
        E[i] = abr.rechercher_voisins(points, i, distance)
    #Parcours en profondeur pour compter les parties connexes
    composantes_connexes = []
    def parcours_rec_partie_connexe(index_pts):
        vus.add(index_pts)
        nbr_pts = 1
        for index_voisin in E[index_pts]:
            if index_voisin not in vus:
                nbr_pts += parcours_rec_partie_connexe(index_voisin)
        return nbr_pts
    vus = set()
    for index_pts in range(n):
        if index_pts not in vus:
            composantes_connexes.append(parcours_rec_partie_connexe(index_pts))
    #tri et affichage des tailles
    print(sorted(composantes_connexes, reverse = True))



def print_components_sizes(distance, points):
    """Renvoie les tailles des parties connexes avec un parcours en profondeur"""
    points_triee = sorted(points, key=lambda p: p.coordinates[0])
    n = len(points)
    vus = set()
    E = {}
    for index_pts in range(n):
        E[index_pts] = set()
    for index_pts in range(n):
        index_candidat = index_pts + 1
        while index_candidat < n and points_triee[index_candidat].coordinates[0] - points_triee[index_pts].coordinates[0] <= distance:
            if points_triee[index_candidat].distance_to(points_triee[index_pts]) <= distance:
                E[index_pts].add(index_candidat)
                E[index_candidat].add(index_pts)
            index_candidat += 1
        index_candidat = index_pts - 1
        while index_candidat > -1 and points_triee[index_pts].coordinates[0] - points_triee[index_candidat].coordinates[0] <= distance:
            if points_triee[index_candidat].distance_to(points_triee[index_pts]) <= distance:
                E[index_pts].add(index_candidat)
                E[index_candidat].add(index_pts)
            index_candidat -= 1 
    #Parcours en profondeur utilisant une pile pour compter les parties connexes
    tailles_composantes_connexes = []
    for index_pts in range(n):
        if index_pts not in vus:
            taille = 0
            stack = [index_pts]
            while stack:
                index_cur = stack.pop()
                if index_cur in vus:
                    continue
                vus.add(index_cur)
                taille += 1
                for index_voisin in E[index_cur]:
                    if index_voisin in vus:
                        continue
                    stack.append(index_voisin)
            tailles_composantes_connexes.append(taille)
    print(sorted(tailles_composantes_connexes, reverse = True))
    
def print_components_sizes_forêt(distance, points):
    """Renvoie les tailles des parties connexes ave la SDD EnsemblesDisjoints"""
    points_triee = sorted(points, key=lambda p: p.coordinates[0])
    n = len(points)
    E = EnsemblesDisjoints(n)
    for index_pts in range(n):
        index_candidat = index_pts + 1
        while index_candidat < n and points_triee[index_candidat].coordinates[0] - points_triee[index_pts].coordinates[0] <= distance:
            if points_triee[index_candidat].distance_to(points_triee[index_pts]) <= distance:
                E.union(index_candidat, index_pts)
            index_candidat += 1
        index_candidat = index_pts - 1
        while index_candidat > -1 and points_triee[index_pts].coordinates[0] - points_triee[index_candidat].coordinates[0] <= distance:
            if points_triee[index_candidat].distance_to(points_triee[index_pts]) <= distance:
                E.union(index_candidat, index_pts)
            index_candidat -= 1 
    tailles_composantes_connexes = sorted([taille for taille in E.taille if taille != 0], reverse=True)
    print(tailles_composantes_connexes)


def load_instance(filename):
    """
    loads .pts file.
    returns distance limit and points.
    """

    with open(filename, "r") as instance_file:
        lines = iter(instance_file)
        distance = float(next(lines))
        points = [Point([float(f) for f in l.split(",")]) for l in lines]

    return distance, points


def main():
    """
    ne pas modifier: on charge une instance et on affiche les tailles
    """
    for instance in argv[1:]:
        distance, points = load_instance(instance)
        print_components_sizes(distance, points)


main()

from geo.point import Point
from geo.UnionFind import UnionFind

class ABR_uf:
    def __init__(self, racine = None):
        self.racine = racine 

    def rechercher_voisins(self, points, i, distance, uf):
        self.recherche_recursive_voisins(self.racine, points, i, distance, uf)
    def recherche_recursive_voisins(self, noeud, points, i, distance, uf):
        if noeud == None:
            return
        if(points[i].distance_to(points[noeud.valeur]) <= distance):
            uf.union(i,noeud.valeur)
        if points[noeud.valeur].coordinates[0] > points[i].coordinates[0]:
            self.recherche_recursive_voisins(noeud.gauche, points, i, distance, uf)
            if points[noeud.valeur].coordinates[0] - points[i].coordinates[0] <= distance:
                self.recherche_recursive_voisins(noeud.droit, points, i, distance, uf) 
        else:
            self.recherche_recursive_voisins(noeud.droit, points, i, distance, uf)
            if points[i].coordinates[0] - points[noeud.valeur].coordinates[0] <= distance:
                self.recherche_recursive_voisins(noeud.gauche, points, i, distance, uf)
                
    def insert(self, points, i):
        if self.racine is None:
            self.racine = Noeud(i)
        else:
            self.insert_rec(self.racine, points, i)
    def insert_rec(self, noeud, points, i):
        if points[i].coordinates[0] < points[noeud.valeur].coordinates[0]:
            if noeud.gauche is None:
                noeud.gauche = Noeud(i)
            else:
                self.insert_rec(noeud.gauche, points, i)
        else:
            if noeud.droit is None:
                noeud.droit = Noeud(i)
            else:
                self.insert_rec(noeud.droit, points, i)
    


class Noeud:
    def __init__(self, valeur, noeud_gauche = None, noeud_droit = None):
        self.valeur = valeur
        self.gauche = noeud_gauche
        self.droit = noeud_droit
    

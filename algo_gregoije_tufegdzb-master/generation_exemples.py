import sys
import random

def generate_data_file(n,d):
    with open(f"exemple_{n}_pts.txt", "w") as file:
        # Écriture du premier nombre fixe
        line = f"{d}\n"
        file.write(line)

        # Génération des coordonnées aléatoires
        for _ in range(n):
            x = random.random()
            y = random.random()
            line = f"{x}, {y}\n"
            file.write(line)

def main():
    # Vérification du nombre d'arguments
    if len(sys.argv) != 3:
        print("Veuillez fournir deux argument : la taille n du fichier et la distance d")
        return

    # Récupération de la taille n depuis les arguments du terminal
    n = int(sys.argv[1])
    d = float(sys.argv[2])

    # Génération du fichier de données
    generate_data_file(n,d)

    print(f"Le fichier exemple_{n}_pts.txt a été généré avec succès avec {n} lignes.")

if __name__ == "__main__":
    main()
package com.nsweb.heroapp.data.database;

import com.nsweb.heroapp.data.domain.SuperHero;


import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

// a repository for calling a local database to store data locally
@Singleton
public class SuperHeroDatabase {

    // a constructor for dependency injection
    @Inject
    public SuperHeroDatabase() { }
    // implementing methods for doing CRUD operations on the data (create, retrieve, update, delete)

    // creating a super hero
    public long insertSuperHero(SuperHero superHero) {
        return superHero.save();
    }

    // retrieving a super hero via id
    public SuperHero getSuperHeroById(long id) {
        return SuperHero.findById(SuperHero.class, id);
    }

    // retrieving all superheroes
    public List<SuperHero> getAllSuperHeroes() {
        return SuperHero.find(SuperHero.class, null, (String[]) null);
    }

    // return the number of heroes in the database
    public long getSuperHeroCount() {
        return SuperHero.count(SuperHero.class);
    }

    // updating the superhero in the database
    public long updateSuperHero(SuperHero superHero) {
        SuperHero updatedSuperHero = SuperHero.findById(SuperHero.class, superHero.getId());
        updatedSuperHero.setName(superHero.getName());
        updatedSuperHero.setDescription(superHero.getDescription());
        updatedSuperHero.setPrimaryPower(superHero.getPrimaryPower());
        updatedSuperHero.setSecondaryPower(superHero.getSecondaryPower());
        updatedSuperHero.setImageUri(superHero.getImageUri());

        return updatedSuperHero.save();
    }
    // this overloaded method is for updating a superhero in both the database and the rest api
    public long updateSuperHero(SuperHero superHero, long position) {
        SuperHero updatedSuperHero = SuperHero.findById(SuperHero.class, position);
        updatedSuperHero.setName(superHero.getName());
        updatedSuperHero.setDescription(superHero.getDescription());
        updatedSuperHero.setPrimaryPower(superHero.getPrimaryPower());
        updatedSuperHero.setSecondaryPower(superHero.getSecondaryPower());
        updatedSuperHero.setImageUri(superHero.getImageUri());

        return updatedSuperHero.save();
    }

    // deleting the superhero in the database
    public void deleteSuperHero(SuperHero superHero) {
        SuperHero superHeroToDelete = SuperHero.findById(SuperHero.class, superHero.getId());
        superHeroToDelete.delete();
    }

    // delete superhero in both the database and the rest api
    public void deleteSuperHero(long id) {
        SuperHero superHero = SuperHero.findById(SuperHero.class, id);
        superHero.delete();
    }

    // finding the superhero with his name
    public List<SuperHero> getSuperHeroByName(String name) {
        return SuperHero.findWithQuery(SuperHero.class, "select * from SUPER_HERO where name like ?", "%" + name + "%");
    }

    // trying to update the sequence of the SQLite database
    public void updateSequence(long id, String table) {
        SuperHero.executeQuery("update SQLITE_SEQUENCE set seq = ? where name = ?", String.valueOf(id), table);
    }
}

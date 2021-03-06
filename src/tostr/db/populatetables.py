import cgi
from random import randint
import cgitb; cgitb.enable()

form = cgi.FieldStorage()

import MySQLdb
import csv

# Feed in all creators, likes, pictures, and users to the DB, then update likes in the pictures table.
def populatetables(cursor):
    c=open("creators.csv")
    for row in csv.reader(c):
        if not row[0]=='uid':
            addcreator(cursor, row[0],row[1])

    l=open("likes.csv")
    for row in csv.reader(l):
        if not row[0]=='uid':
            addlike(cursor, row[0],row[1])

    p=open("pictures.csv")
    for row in csv.reader(p):
        if not row[0]=='id':
            addpicture(cursor, row[0],row[1],row[2],row[3],row[4])

    u=open("users.csv")
    for row in csv.reader(u):
        if not row[0]=='id':
            adduser(cursor, row[0],row[1],row[2],row[3],row[4])

    updatelikes(cursor)

# Adds a creator from the csv file to the db
def addcreator(cursor, uid, pid):
    try:
        cursor.execute("INSERT INTO creators VALUES(%s, %s)", (uid, pid))
        conn.commit()
    except:
        conn.rollback()
        print "rollback"

# Adds a like from the csv to the db
def addlike(cursor, uid, pid):
    try:
        cursor.execute("INSERT INTO likes VALUES(%s, %s)", (uid, pid))
        conn.commit()
    except:
        conn.rollback()
        print "rollback"

# Adds a picture from the csv file to the db
def addpicture(cursor, uid, creatorid, likes, date, name):
    try:
        cursor.execute("INSERT INTO pictures VALUES(%s, %s, %s, %s, %s)", (uid, creatorid, likes, date, name))
        conn.commit()
    except:
        conn.rollback()
        print "rollback"

# Adds a user from the csv file to the db
def adduser(cursor, uid, uname, bday, urealfirst, ureallast):
    try:
        cursor.execute("INSERT INTO users VALUES(%s, %s, %s, %s, %s)", (uid, uname, bday, urealfirst, ureallast))
        conn.commit()
    except:
        conn.rollback()
        print "rollback"

# Updates all of the likes to be the sum of the likes in the likes table when the pid is equal.
def updatelikes(cursor):
    try:
        cursor.execute("Update pictures SET likes = (select count(uid) from likes where likes.pid = pictures.id)")
        conn.commit()
    except:
        conn.rollback()
        print "rollback"

conn = MySQLdb.connect (host = "localhost",
                          user = "root",
                        passwd = "password",
                        db = "connor")

cursor = conn.cursor()
populatetables(cursor)
cursor.close()
conn.close()

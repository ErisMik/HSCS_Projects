# Eric Mikulin, Computer Science 302
# Python Backend for the ITDB

# Standard imports
import sys, getopt
import os
import MySQLdb

# PDF miner imports
from pdfminer.pdfinterp import PDFResourceManager, PDFPageInterpreter
from pdfminer.converter import TextConverter
from pdfminer.layout import LAParams
from pdfminer.pdfpage import PDFPage
from cStringIO import StringIO

# This function recursivly calls on every PDF document in the folder "papers"
def search_Files(tags):
    # Recursivly walk through all the Folders, and files
    walk_dir = "../papers"  # Starting directory
    for root, subdirs, files in os.walk(walk_dir):  # For every directory, sub-directory and file in the whole path
        list_file_path = os.path.join(root, 'my-directory-list.txt')
        with open(list_file_path, 'wb') as list_file:  # Open the last subdirectory
            for subdir in subdirs:
                print(".")
            for filename in files:  # For every file in the sub-directory
                file_path = os.path.join(root, filename)  # Get the absolute file path
                # If PDF is it's type, attempt to parse
                if "pdf" in filename:
                    read_PDF(file_path, tags)

# This document attempts to parse the PDF, then checks if it makes a search tag
def read_PDF(path, tags):
    # Set up for PDFMiner to parse the PDF
    rsrcmgr = PDFResourceManager()
    retstr = StringIO()
    codec = 'utf8'
    laparams = LAParams()
    device = TextConverter(rsrcmgr, retstr, codec=codec, laparams=laparams)
    fp = file(path, 'rb')
    interpreter = PDFPageInterpreter(rsrcmgr, device)
    password = ""
    maxpages = 2
    caching = True
    pagenos=set()

    # For every page in the PDF document attempt to process the page
    for page in PDFPage.get_pages(fp, pagenos, maxpages=maxpages, password=password, caching=caching, check_extractable=True):
        interpreter.process_page(page)
    text = retstr.getvalue()  # Get the processed data back as a string

    for tag in tags:  # For every search tag
        if tag.lower() in text.lower():  # If the tag appears somewhere in the text
            addTo_DB(path, tag)  # Add it to the MySQL database

    # PDFMiner clean up
    fp.close()
    device.close()
    retstr.close()
    print("Read " + path)  # Debug print

# This method adds the filepath of the PDF and the search tag to the MySQL database
def addTo_DB(path, tag):
    # Connect to the Local MySQL database
    database = MySQLdb.connect("localhost", "root", "","cs302")
    cursor = database.cursor()

    # Execute the MySQL code adding the path and tag to the key pair database called PAPERS
    sql = "INSERT INTO PAPERS (`path`, `tag`) VALUES (\"{0}\", \"{1}\");".format(path, tag)
    print("Attempting to add to MySQL database")
    try:
        cursor.execute(sql)  # Add the SQL insert to a queue
        database.commit()  # Process the queue
    except:
        print("Adding failed")

# This method searches through the MySQL database
def search_DB(tags):
    # Connect to the Local MySQL database
    database = MySQLdb.connect("localhost", "root", "","cs302")
    cursor = database.cursor()

    # Get all the seperate lists from the Database
    all_sets = []  # Create a master set of sets
    for tag in tags:  # For each of the search tags
        new_set = []  # Create a set (Temporary)

        # Execute the SQL seach
        sql = "SELECT * FROM PAPERS WHERE tag=\"{0}\"".format(tag,)
        cursor.execute(sql)
        pairs = cursor.fetchall()  # Get the results back from the server as a list


        for row in pairs:  # For every element of the list pairs
            new_set.append(row[0])  # Add the paper filepath to the temporary set 
        all_sets.append(new_set)  # Add the temporary set as an elemnt into the master set 

    # Intersect all the lists
    trimmed_sets = all_sets[0]  # Create an initial set
    for aset in all_sets:  # For all the subsets in the master set
        trimmed_sets = list(set(trimmed_sets) & set(aset))  # Intersect them together

    print("\n\n=============================================")  # Formatting print
    # Print out for the user to use
    for path in trimmed_sets:  # For all the filepaths to the PDFS from the intesected sets
        print("Paper that meets search criteria:\n"+path+"\n")

    # sortedTree = binaryTree(len(trimmed_sets[0]), trimmed_sets[0])
    # for path in trimmed_sets:
    #     sortedTree.addNode(len(path), path)
    # sortedTree.printAll(sortedTree.rootNode)

# Binary search tree class
class binaryTree:
    def __init__(self, dataInitial, pathInitial):
        self.rootNode = binaryNode(dataInitial, pathInitial)

    def addNode(self, data, path):
        currentNode = self.rootNode
        finished = False
        while not finished:
            curLeftNode = currentNode.leftNode
            curRightNode = currentNode.rightNode
            curData = currentNode.data
            if data > curData:
                if curRightNode == None :
                    currentNode.rightNode = binaryNode(data, path)
                else:
                    currentNode = currentNode.rightNode
            else:
                if curLeftNode == None :
                    currentNode.leftNode = binaryNode(data, path)
                else:
                    currentNode = currentNode.leftNode

    def printAll(self, node):
        if (node == None):
            return
        self.printAll(node.leftNode)
        print("Paper that matches Query:\n" + node.path + "\n\n")
        self.printAll(node.rightNode)

# Binary node tree class
class binaryNode:
    def __init__(self, data, path):
        self.leftNode = None
        self.rightNode = None
        self.data = data
        self.path = path

# This is the MAIN method, it is run when the script is called
if __name__ == "__main__":
    # Get the arguments when the Python script is called
    try:
        opts, args = getopt.getopt(sys.argv[1:], 'i:y:s:t', ['idnum=', 'years=', 'subject=', 'text='])  # Define the possible arguments
    except getopt.GetoptError:
        sys.exit(2)
    values = []
    # Check that each argument exists, if it does, add it's value to the list of values (These will be the search terms)
    for opt, arg in opts:
        if opt in ('-i', '--idnum'):
            values.append(arg)
        if opt in ('-y', '--years'):
            values.append(arg)
        if opt in ('-s', '--subject'):
            values.append(arg.lower())
        if opt in ('-t', '--text'):
            vals = arg.split(",")
            for it in vals:
                values.append(it.strip().lower())

    for vla in values:
        print vla

    # Connect to the Local MySQL database
    database = MySQLdb.connect("localhost", "root", "","cs302")
    cursor = database.cursor()
    
    searchPapers = False
    unfound = []
    # Check each search tag to so if it has been searched before. If it hasn't, parse all the papers and add them to the database
    for tag in values:
        sql = "SELECT * FROM PAPERS WHERE tag=\"{0}\"".format(tag,)
        entries = cursor.execute(sql)
        if entries == 0:
            searchPapers = True
            unfound.append(tag)

    if searchPapers:  # If an unfound tag exists, parse the papers (This takes a few mins so that's why it isn't always done)
        search_Files(unfound) 

    search_DB(values)  # Regardless, search through the Database

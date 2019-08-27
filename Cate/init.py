from VH import *
import math

import sys
sys.path.append("/sdcard/vhdata/pythonlibs")
print sys.modules.keys()

print "hola"

characterName = "ChrLindsay"
bmlID = 0
emotion = "HAPPY"

def saySomething(actor, message, emotion0):
    global  emotion

    end = 30
    start = 2
    ready = start + 1
    relax = end - 10

    intensity = 1

    if emotion0 == emotion and intensity < 3:
        intensity += 0.7
    elif emotion0 != emotion:
        intensity = 1
        emotion = emotion0

    from xml.etree import ElementTree
    stopTalking(actor)
    bmlMsg = androidEngine.getNonverbalBehavior(message)
    root = ElementTree.XML(bmlMsg)
    rootBML = root.find("bml")
    faces = []
    marks = str(len(rootBML.find("speech").findall("mark")) - 1)

    print "emotion: " + emotion0

    if emotion0 == "HAPPY":
        # HAPPY
        face0 = ElementTree.Element("face")
        face0.set("au", "7")
        face0.set("amount", str(0.5 * intensity))
        face0.set("start", str(start))
        face0.set("type", "facs")
        faces.append(face0)

        face1 = ElementTree.Element("face")
        face1.set("au", "14")
        face1.set("amount", str(0.4 * intensity))
        face1.set("type", "facs")
        face1.set("start", str(start))
        face1.set("end", str(end))
        faces.append(face1)

        face2 = ElementTree.Element("face")
        face2.set("au", "6")
        face2.set("amount", str(0.6 * intensity))
        face2.set("type", "facs")
        face2.set("start", str(start))
        face2.set("end", str(end))
        faces.append(face2)

        face3 = ElementTree.Element("face")
        face3.set("au", "1")
        face3.set("amount", str(1.0 * intensity))
        face3.set("type", "facs")
        face3.set("start", str(start))
        face3.set("ready", str(ready))
        face3.set("relax", str(relax))
        face3.set("end", str(end))
        faces.append(face3)

        face4 = ElementTree.Element("face")
        face4.set("au", "2")
        face4.set("amount", str(1.0 * intensity))
        face4.set("type", "facs")
        face4.set("start", str(start))
        face4.set("ready", str(ready))
        face4.set("relax", str(relax))
        face4.set("end", str(end))
        faces.append(face4)

    elif emotion0 == "CONCERNED":
        # CONCERNED
        face0 = ElementTree.Element("face")
        face0.set("au", "1")
        face0.set("amount", str(1.0 * intensity))
        face0.set("start", str(start))
        face0.set("ready", str(ready))
        face0.set("relax", str(relax))
        face0.set("end", str(end))
        face0.set("type", "facs")
        faces.append(face0)

        face1 = ElementTree.Element("face")
        face1.set("au", "15")
        face1.set("amount", str(1.0 * intensity))
        face1.set("start", str(start))
        face1.set("end", str(end))
        face1.set("type", "facs")
        faces.append(face1)

        face2 = ElementTree.Element("face")
        face2.set("au", "17")
        face2.set("amount", str(0.9 * intensity))
        face2.set("start", str(start))
        face2.set("end", str(end))
        face2.set("type", "facs")
        faces.append(face2)

        face3 = ElementTree.Element("face")
        face3.set("au", "2")
        face3.set("amount", str(-1.0 * intensity))
        face3.set("start", str(start))
        face3.set("end", str(end))
        face3.set("start", str(start))
        face3.set("ready", str(ready))
        face3.set("relax", str(relax))
        face3.set("type", "facs")
        faces.append(face3)

    elif emotion0 == "WORRIED":
        # WORRIED
        face0 = ElementTree.Element("face")
        face0.set("au", "1")
        face0.set("amount", str(1.0 * intensity))
        face0.set("end", str(end))
        face0.set("start", str(start))
        face0.set("ready", str(ready))
        face0.set("relax", str(relax))
        face0.set("type", "facs")
        faces.append(face0)

        face1 = ElementTree.Element("face")
        face1.set("au", "15")
        face1.set("amount", str(0.7 * intensity))
        face1.set("end", str(end))
        face1.set("start", str(start))
        face1.set("ready", str(ready))
        face1.set("relax", str(relax))
        face1.set("type", "facs")
        faces.append(face1)

        face2 = ElementTree.Element("face")
        face2.set("au", "17")
        face2.set("amount", str(0.9 * intensity))
        face2.set("start", str(start))
        face2.set("end", str(end))
        face2.set("type", "facs")
        faces.append(face2)

        face3 = ElementTree.Element("face")
        face3.set("au", "2")
        face3.set("amount", str(-1.0 * intensity))
        face3.set("end", str(end))
        face3.set("start", str(start))
        face3.set("ready", str(ready))
        face3.set("relax", str(relax))
        face3.set("type", "facs")
        faces.append(face3)

        face4 = ElementTree.Element("face")
        face4.set("au", "10")
        face4.set("amount", str(-0.8 * intensity))
        face4.set("end", str(end))
        face4.set("start", str(start))
        face4.set("ready", str(ready))
        face4.set("relax", str(relax))
        face4.set("type", "facs")
        faces.append(face4)

        face5 = ElementTree.Element("face")
        face5.set("au", "18")
        face5.set("amount", str(0.25 * intensity))
        face5.set("end", str(end))
        face5.set("start", str(start))
        face5.set("ready", str(ready))
        face5.set("relax", str(relax))
        face5.set("type", "facs")
        faces.append(face5)

    for face in  rootBML.findall("face"):
        rootBML.remove(face)

    for face in faces:
        rootBML.append(face)

    bmlID = bml.execXML(actor, ElementTree.tostring(root, encoding="utf-8"))


def doHappy():
    from random import *
    mouth = cheek = eyebrow = 0


    mouth = 0.90
    cheek = 0.65
    eyebrow = 1
    lipGap = 0.9


    mouthRandom = uniform(mouth, mouth + 0.05)
    cheekRandom = uniform(cheek, cheek + 0.05)
    eyebrow2Random = uniform(eyebrow, eyebrow + 0.15)
    #the inner should always be higher or else it will look angry / fearful / worried
    eyebrow1Random = uniform(eyebrow2Random, eyebrow + 0.1)
    lidRandom = uniform(0.35, 0.65)
    lipGapRandom = uniform(lipGap, lipGap + 0.2)

    bmlExec = '<face type="FACS" stroke="3" au="1" velocity="0.5" amount="' + str(eyebrow1Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="2" amount="'+ str(eyebrow2Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="6" amount="' + str(cheekRandom) +'" /><face type="FACS" stroke="3" velocity="0.5" au="12" amount="' + str(mouthRandom) + '" />'
    bmlID = bml.execBML(characterName, bmlExec)


def doConcern():
    from random import *
    mouth = cheek = eyebrow = 0


    mouth = 0.1
    cheek = 0.65
    eyebrow = 1
    lipGap = 0.9


    mouthRandom = uniform(mouth, mouth + 0.05)
    cheekRandom = uniform(cheek, cheek + 0.05)
    eyebrow2Random = uniform(eyebrow, eyebrow + 0.15)
    #the inner should always be higher or else it will look angry / fearful / worried
    eyebrow1Random = uniform(eyebrow2Random, eyebrow + 0.1)
    lidRandom = uniform(0.35, 0.65)
    lipGapRandom = uniform(lipGap, lipGap + 0.2)

    bmlExec = '<face type="FACS" stroke="3" au="1" velocity="0.5" amount="' + str(eyebrow1Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="2" amount="'+ str(eyebrow2Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="6" amount="' + str(cheekRandom) +'" /><face type="FACS" stroke="3" velocity="0.5" au="12" amount="' + str(mouthRandom) + '" />'
    bmlID = bml.execBML(characterName, bmlExec)

def doWorried():
    from random import *
    mouth = cheek = eyebrow = 0


    mouth = 0.2
    cheek = 0.65
    eyebrow = 1
    lipGap = 0.9


    mouthRandom = uniform(mouth, mouth + 0.05)
    cheekRandom = uniform(cheek, cheek + 0.05)
    eyebrow2Random = uniform(eyebrow, eyebrow + 0.15)
    #the inner should always be higher or else it will look angry / fearful / worried
    eyebrow1Random = uniform(eyebrow2Random, eyebrow + 0.1)
    lidRandom = uniform(0.35, 0.65)
    lipGapRandom = uniform(lipGap, lipGap + 0.2)

    bmlExec = '<face type="FACS" stroke="3" au="1" velocity="0.5" amount="' + str(eyebrow1Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="2" amount="'+ str(eyebrow2Random) + '" /><face type="FACS" stroke="3" velocity="0.5" au="6" amount="' + str(cheekRandom) +'" /><face type="FACS" stroke="3" velocity="0.5" au="12" amount="' + str(mouthRandom) + '" />'
    bmlID = bml.execBML(characterName, bmlExec)

def doBehavior(actor, message):
    bmlMsg = androidEngine.getBehavior(message, 0, 5)
    bmlID = bml.execBML(actor, bmlMsg)
    print bmlMsg

def stopTalking(actor):
    bml.interruptCharacter(actor, bmlID)

    print "interrupt talking"
    bml.execBML(characterName, '<body posture="ChrBrad@Idle01"/>')
    bml.execBML(characterName, '<saccade mode="listen"/>')


def setToHappy(characterName, intensity):
    emotion = intensity

def setToConcern(characterName, intensity):
    emotion = intensity - 3

class MyVHEngine(VHEngine):
    def eventInit(self):
        print "Starting VH engine..."
        self.initTextToSpeechEngine('cerevoice') # initialize tts engine for speech synthesis

    def eventInitUI(self):
        self.setBackgroundImage('/sdcard/vhdata/office2.png')

androidEngine = MyVHEngine()
setVHEngine(androidEngine)

scene.addAssetPath("script", "scripts")
scene.addAssetPath("motion", "characters")
scene.setBoolAttribute("internalAudio", True)
scene.run("camera.py")
scene.run("light.py")
scene.run("setupCharacter.py")
setupCharacter(characterName, characterName, "", "")
character = scene.getCharacter(characterName)
bml.execBML(characterName, '<body posture="ChrBrad@Idle01"/>')
bml.execBML(characterName, '<saccade mode="listen"/>')
sim.start()

# start the first utterance
#scene.command('bml char ChrRachel file ./Sounds/intro_age_1.xml')

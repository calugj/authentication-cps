#import "@preview/chronos:0.2.0"

#set par(justify: true)
#set heading(numbering: "1.")
#set math.vec(delim: "[")
#set math.mat(delim: "[")
#set page(columns: 2)
#show raw: set text(size: 7pt)
#show figure.caption: emph
#show figure.caption: set text(size: 9pt)


#show ". ": ".   "
#show "i.e.": "i.e."
#show "e.g.": "e.g."
#show "etc.": "etc."




#place(
  top + center,
  scope: "parent",
  float: true,
  align(center)[
    #block(width:85%)[
      #text(size:22pt, weight:"semibold", "Report for the Course on Cyber-Physical Systems and IoT Security")

      #text(size:17pt, style:"italic", "Authentication of IoT Device and IoT Server Using Secure Vaults")

      #text(size:14pt, "Boscolo Meneguolo Luca  -  2113488")
      
      #linebreak()
    ]
  ]
)





= Objective


= System Setup


= Experiments


= Results and Discussion


= Conclusions

#text()[ciao]


#chronos.diagram({
  import chronos: *
  _par("IoT")
  _par("Server")

  _seq("IoT", "Server", comment: "ajeje")
})





#show ".": "."
#linebreak()
#bibliography("biblio.yml")
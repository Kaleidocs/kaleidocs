uberjar:
	lein do deps, bower install, cl2c once frontend, clean, uberjar
deploy:
	mkdir deploy
	cp ~/.m2/repository/fr/opensagres/xdocreport/fr.opensagres.xdocreport.document.odt/1.0.3/fr.opensagres.xdocreport.document.odt-1.0.3.jar deploy
	cp ~/.m2/repository/fr/opensagres/xdocreport/fr.opensagres.xdocreport.document.ods/1.0.3/fr.opensagres.xdocreport.document.ods-1.0.3.jar deploy
	cp _start deploy/start.sh
	cp _start deploy/start.bat
	cp target/kaleidocs.jar deploy
